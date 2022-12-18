package kr.co.marketbill.marketbillcoreserver.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.MessageType
import kr.co.marketbill.marketbillcoreserver.domain.dto.MessageReqDto
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.domain.dto.ReceiptProcessInput
import kr.co.marketbill.marketbillcoreserver.domain.dto.ReceiptProcessOutput
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetReceiptRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderItemSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderSheetReceiptSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderSheetSpecs
import kr.co.marketbill.marketbillcoreserver.graphql.error.InternalErrorException
import kr.co.marketbill.marketbillcoreserver.graphql.error.NotFoundException
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput
import kr.co.marketbill.marketbillcoreserver.util.StringGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.text.SimpleDateFormat
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

    @Autowired
    private lateinit var messagingService: MessagingService

    @Value("\${serverless.file-process.host}")
    private lateinit var receiptProcessHost: String

    val logger: Logger = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun orderCartItems(retailerId: Long): OrderSheet {

        val cartItems = cartRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
            .map {
                it.orderedAt = LocalDateTime.now()
                it
            }.get().toList()

        if (cartItems.isEmpty()) throw NotFoundException("There's no cart items to order.")
        cartRepository.saveAll(cartItems)

        val isAllConnectedWithWholesaler = cartItems.mapNotNull { it.wholesaler }.size == cartItems.size
        if (!isAllConnectedWithWholesaler) throw NotFoundException("There's no connected wholesaler on cart items.")

        val selectedRetailer: User = cartItems[0].retailer!!
        val selectedWholesaler: User = cartItems[0].wholesaler!!

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


    fun getAllOrderItemsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<OrderItem>> {
        val orderItems = orderItemRepository.findAll(OrderItemSpecs.byOrderSheetIds(orderSheetIds), pageable)
        return orderItems.groupBy { it.orderSheet!!.id!! }
            .toMutableMap()
    }

    fun getAllOrderSheetReceiptsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<OrderSheetReceipt>> {
        val orderSheetReceipts =
            orderSheetReceiptRepository.findAll(OrderSheetReceiptSpecs.byOrderSheetIds(orderSheetIds), pageable)
        return orderSheetReceipts.groupBy { it.orderSheet!!.id!! }
            .toMutableMap()
    }

    fun getOrderSheet(orderSheetId: Long): OrderSheet {
        val orderSheet: Optional<OrderSheet> = orderSheetRepository.findById(orderSheetId)
        if (orderSheet.isEmpty) {
            throw NotFoundException(message = "There's no order sheet data whose id is $orderSheetId")
        } else {
            return orderSheet.get()
        }
    }

    fun removeOrderSheet(orderSheetId: Long): Long {
        try {
            val orderSheet = orderSheetRepository.findById(orderSheetId)
            if (orderSheet.isEmpty) {
                throw NotFoundException(message = "There's no order sheet data want to delete")
            }
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

    fun getDailyOrderSheetsAggregate(wholesalerId: Long, dateStr: String?): Optional<OrderSheetsAggregate> {
        var curDate = Date()
        if (dateStr != null) {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            curDate = formatter.parse(dateStr)
        }
        val aggregation = orderSheetRepository.getDailyOrderSheetsAggregate(wholesalerId, curDate)
        return if (aggregation.getDate() == null) {
            Optional.empty()
        } else {
            Optional.of(aggregation)
        }

    }

    @Transactional
    fun issueOrderSheetReceipt(orderSheetId: Long): OrderSheetReceipt {
        val orderSheet: Optional<OrderSheet> = orderSheetRepository.findById(orderSheetId)
        if (orderSheet.isEmpty) throw NotFoundException("There's no OrderSheet data whose id is $orderSheetId")


        val hasNullPrice = orderSheet.get().orderItems.any { it.price == null }
        if(hasNullPrice){
            throw InternalErrorException(message = "Not able to process receipt with order item which has no price data.")
        }
        val input = ReceiptProcessInput(
            orderNo = orderSheet.get().orderNo,
            retailer = ReceiptProcessInput.Username(name = orderSheet.get().retailer!!.name!!),
            wholesaler = ReceiptProcessInput.Username(name = orderSheet.get().wholesaler!!.name!!),
            orderItems = orderSheet.get().orderItems.map {
                ReceiptProcessInput.OrderItem(
                    flower = ReceiptProcessInput.Flower(
                        name = it.flower!!.name,
                        flowerType = ReceiptProcessInput.FlowerType(name = it.flower!!.flowerType!!.name),
                    ),
                    quantity = it.quantity!!,
                    price = it.price!!,
                    grade = it.grade!!,
                )
            }
        )
        val receiptInfo: ReceiptProcessOutput = runBlocking {
            generateReceipt(input)
        }
        val orderSheetReceipt = OrderSheetReceipt(
            orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId),
            filePath = receiptInfo.filePath,
            fileFormat = receiptInfo.fileFormat,
            metadata = receiptInfo.metadata
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


    suspend fun generateReceipt(input: ReceiptProcessInput): ReceiptProcessOutput {
        val client = createReceiptProcessClient()
        val res = client.post().body(BodyInserters.fromValue(input)).awaitExchange {
            it.awaitBody<ReceiptProcessOutput>()
        }
        return res
    }

    private fun createReceiptProcessClient(): WebClient {
        return WebClient
            .builder()
            .baseUrl(receiptProcessHost)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}