package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderStatisticOutput
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetReceiptRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderItemSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderSheetSpecs
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.EntityManager


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

    val logger: Logger = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun orderCartItems(retailerId: Long): OrderSheet {
        val cartItems = cartRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
            .map {
                it.orderedAt = LocalDateTime.now()
                it
            }.get().toList()

        if(cartItems.isEmpty()){
            throw Exception("There's no cart items to order.")
        }

        cartRepository.saveAll(cartItems)

        val selectedWholesaler = cartItems[0].wholesaler

        val orderItems = cartItems.map {
            OrderItem(
                retailer = it.retailer,
                wholesaler = selectedWholesaler,
                flower = it.flower,
                quantity = it.quantity,
                grade = it.grade,
                price = null,
            )
        }
        val savedOrderItems = orderItemRepository.saveAll(orderItems)

        val orderSheet = OrderSheet(
            orderNo = UUID.randomUUID().toString(),
            retailer = savedOrderItems[0].retailer,
            wholesaler = selectedWholesaler,
            orderItems = savedOrderItems,
        )
        return orderSheetRepository.save(orderSheet)
    }

    fun getOrderSheets(userId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderSheet> {
        return orderSheetRepository.findAll(
            OrderSheetSpecs.byUserId(userId, role).and(OrderSheetSpecs.atDate(date)),
            pageable
        )
    }

    fun getOrderItems(date: LocalDate?, pageable: Pageable): Page<OrderItem> {
        return orderItemRepository.findAll(OrderItemSpecs.atDate(date), pageable)
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

    fun getDailyOrderStatistics(wholesalerId: Long, pageable: Pageable): Page<OrderStatisticOutput> {
        val monthsToSubtract: Long = 3.toLong()

        val curDate = Date()
        val dateBeforeThreeMonth = Date.from(
            LocalDate.now().minusMonths(monthsToSubtract).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        return orderSheetRepository.getAllDailyStatistics(wholesalerId, dateBeforeThreeMonth, curDate, pageable)
    }

    fun issueOrderSheetReceipt(orderSheetId: Long): OrderSheetReceipt {
        val orderSheet: Optional<OrderSheet> = orderSheetRepository.findById(orderSheetId)
        if (orderSheet.isEmpty) throw Exception("There's no OrderSheet data whose id is $orderSheetId")

        val orderItems = orderSheet.get().orderItems

        val orderSheetReceipt = OrderSheetReceipt(
            orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId),
            filePath = "",
            fileFormat = "excel",
            metadata = "{volume : 128KB}"
        )

        return orderSheetReceiptRepository.save(orderSheetReceipt)
    }
}