package kr.co.marketbill.marketbillcoreserver.service

import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetReceiptRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.WholesalerConnectionRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderItemSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderSheetSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.WholesalerConnSpecs
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput
import kr.co.marketbill.marketbillcoreserver.util.StringGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import kotlin.math.pow


@Service
class OrderService {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var orderSheetRepository: OrderSheetRepository

    @Autowired
    private lateinit var orderSheetReceiptRepository: OrderSheetReceiptRepository

    @Autowired
    private lateinit var messagingService: MessagingService

    val logger: Logger = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun orderCartItems(retailerId: Long): OrderSheet {
        val cartItems = cartRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
            .map {
                it.orderedAt = LocalDateTime.now()
                it
            }.get().toList()

        if (cartItems.isEmpty()) {
            throw CustomException("There's no cart items to order.")
        }
        cartRepository.saveAll(cartItems)

        val selectedRetailer = cartItems[0].retailer
        val selectedWholesaler = cartItems[0].wholesaler

        val orderSheet = OrderSheet(
            orderNo = "",
            retailer = selectedRetailer,
            wholesaler = selectedWholesaler,
        )

        val savedOrderSheet = orderSheetRepository.save(orderSheet)
        savedOrderSheet.orderNo = StringGenerator.generateOrderNo(savedOrderSheet.id!!)
        val updatedOrderSheet = orderSheetRepository.save(savedOrderSheet)

        val orderItems = cartItems.map {
            OrderItem(
                retailer = it.retailer,
                orderSheet = updatedOrderSheet,
                wholesaler = selectedWholesaler,
                flower = it.flower,
                quantity = it.quantity,
                grade = it.grade,
                price = null,
            )
        }

        orderItemRepository.saveAll(orderItems)
        return updatedOrderSheet
    }

    fun getOrderSheets(userId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderSheet> {
        return orderSheetRepository.findAll(
            OrderSheetSpecs.byUserId(userId, role).and(OrderSheetSpecs.atDate(date)),
            pageable
        )
    }

    fun getOrderItems(wholesalerId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderItem> {
        return orderItemRepository.findAll(
            OrderItemSpecs.atDate(date).and(OrderItemSpecs.byUserId(wholesalerId, role)),
            pageable
        )
    }


    @Transactional(readOnly = true)
    fun getAllOrderItemsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<OrderItem>> {
        val orderItems = orderItemRepository.findAll(OrderItemSpecs.byOrderSheetIds(orderSheetIds), pageable)
        return orderItems.groupBy { it.orderSheet!!.id!! }
            .toMutableMap()
    }

    fun getOrderSheet(orderSheetId: Long): Optional<OrderSheet> {
        return orderSheetRepository.findById(orderSheetId)
    }

    fun removeOrderSheet(orderSheetId: Long): Long {
        try {
            orderSheetRepository.deleteById(orderSheetId)
            return orderSheetId
        } catch (e: java.lang.Exception) {
            logger.error(e.message)
            throw e
        }
    }

    fun updateOrderItemsPrice(items: List<OrderItemPriceInput>): List<OrderItem> {
        val orderItems: List<OrderItem> = items.map {
            val orderItem = entityManager.getReference(OrderItem::class.java, it.id.toLong())
            orderItem.price = it.price
            orderItem
        }
        return orderItemRepository.saveAll(orderItems)
    }

    fun getAllDailyOrderSheetsAggregates(wholesalerId: Long, pageable: Pageable): Page<OrderSheetsAggregate> {
        val monthsToSubtract: Long = 3.toLong()

        val curDate = Date()
        val dateBeforeThreeMonth = Date.from(
            LocalDate.now().minusMonths(monthsToSubtract).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        return orderSheetRepository.getAllDailyOrderSheetsAggregates(
            wholesalerId,
            dateBeforeThreeMonth,
            curDate,
            pageable
        )
    }

    fun getDailyOrderSheetsAggregate(wholesalerId: Long, dateStr: String?): OrderSheetsAggregate {
        var curDate = Date()
        if (dateStr != null) {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            curDate = formatter.parse(dateStr)
        }

        println("@@@@ $curDate")
        return orderSheetRepository.getDailyOrderSheetsAggregate(wholesalerId, curDate)
    }

    fun issueOrderSheetReceipt(orderSheetId: Long): OrderSheetReceipt {
        val orderSheet: Optional<OrderSheet> = orderSheetRepository.findById(orderSheetId)
        if (orderSheet.isEmpty) throw CustomException("There's no OrderSheet data whose id is $orderSheetId")

        val orderSheetReceipt = OrderSheetReceipt(
            orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId),
            filePath = "",
            fileFormat = "excel",
            metadata = "{volume : 128KB}"
        )
        val createdReceipt = orderSheetReceiptRepository.save(orderSheetReceipt)


        val targetPhoneNo = orderSheet.get().retailer!!.userCredential!!.phoneNo
        val wholesalerName = orderSheet.get().wholesaler!!.name!!
        val orderNo = orderSheet.get().orderNo
        val url = ""

        runBlocking {
            messagingService.sendIssueOrderSheetReceiptSMS(
                to = targetPhoneNo,
                wholesalerName = wholesalerName,
                orderNo = orderNo,
                url
            )
        }

        return createdReceipt
    }


}