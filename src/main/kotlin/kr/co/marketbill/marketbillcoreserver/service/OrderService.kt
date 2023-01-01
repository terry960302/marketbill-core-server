package kr.co.marketbill.marketbillcoreserver.service

import com.netflix.graphql.types.errors.ErrorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.domain.dto.ReceiptProcessInput
import kr.co.marketbill.marketbillcoreserver.domain.dto.ReceiptProcessOutput
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BusinessInfo
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetReceiptRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.BusinessInfoRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderItemSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderSheetReceiptSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderSheetSpecs
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.*
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
    private lateinit var businessInfoRepository: BusinessInfoRepository

    @Autowired
    private lateinit var messagingService: MessagingService

    @Value("\${serverless.file-process.host}")
    private lateinit var receiptProcessHost: String

    private val logger: Logger = LoggerFactory.getLogger(OrderService::class.java)
    private val className = this.javaClass.simpleName

    @Transactional
    fun orderCartItems(retailerId: Long): OrderSheet {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val cartItems = cartRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
                .map {
                    it.orderedAt = LocalDateTime.now()
                    it
                }.get().toList()

            if (cartItems.isEmpty()) throw CustomException(
                message = "There's no cart items to order.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_CART_ITEM
            )

            val isAllConnectedWithWholesaler = cartItems.mapNotNull { it.wholesaler }.size == cartItems.size
            if (!isAllConnectedWithWholesaler) throw CustomException(
                message = "There's no connected wholesaler on cart items.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_CART_WHOLESALER
            )
            logger.debug("$className.$executedFunc >> All cart items have wholesaler info.")

            cartRepository.saveAll(cartItems)
            logger.debug("$className.$executedFunc >> All cart items are ordered.")

            val selectedRetailer: User = cartItems[0].retailer!!
            val selectedWholesaler: User = cartItems[0].wholesaler!!

            val orderSheet = OrderSheet(
                orderNo = "",
                retailer = selectedRetailer,
                wholesaler = selectedWholesaler,
            )
            val savedOrderSheet = orderSheetRepository.save(orderSheet)
            logger.debug("$className.$executedFunc >> OrderSheet is created.")
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
            logger.debug("$className.$executedFunc >> Order items are created using cart items.")
            logger.info("$className.$executedFunc >> completed.")
            return updatedOrderSheet
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    fun getOrderSheets(userId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderSheet> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderSheets = orderSheetRepository.findAll(
                OrderSheetSpecs.byUserId(userId, role).and(OrderSheetSpecs.atDate(date)),
                pageable
            )
            logger.info("$className.$executedFunc >> completed.")
            return orderSheets
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun getOrderItems(wholesalerId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val orderItems = orderItemRepository.findAll(
                OrderItemSpecs.atDate(date).and(OrderItemSpecs.byUserId(wholesalerId, role)),
                pageable
            )
            logger.info("$className.$executedFunc >> completed.")
            return orderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }


    fun getAllOrderItemsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<OrderItem>> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val orderItems = orderItemRepository.findAll(OrderItemSpecs.byOrderSheetIds(orderSheetIds), pageable)
            val groupedOrderItems = orderItems.groupBy { it.orderSheet!!.id!! }
                .toMutableMap()
            logger.info("$className.$executedFunc >> completed.")
            return groupedOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun getAllOrderSheetReceiptsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<OrderSheetReceipt>> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val orderSheetReceipts =
                orderSheetReceiptRepository.findAll(OrderSheetReceiptSpecs.byOrderSheetIds(orderSheetIds), pageable)
            val groupedOrderItems = orderSheetReceipts.groupBy { it.orderSheet!!.id!! }
                .toMutableMap()
            logger.info("$className.$executedFunc >> completed.")
            return groupedOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun getOrderSheet(orderSheetId: Long): OrderSheet {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderSheet: Optional<OrderSheet> = orderSheetRepository.findById(orderSheetId)
            if (orderSheet.isEmpty) {
                throw CustomException(
                    message = "There's no order sheet data whose id is $orderSheetId",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_ORDER_SHEET
                )
            } else {
                logger.info("$className.$executedFunc >> completed.")
                return orderSheet.get()
            }
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun removeOrderSheet(orderSheetId: Long): Long {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderSheet = orderSheetRepository.findById(orderSheetId)
            if (orderSheet.isEmpty) {
                throw CustomException(
                    message = "There's no order sheet data want to delete",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_ORDER_SHEET
                )
            }
            orderSheetRepository.deleteById(orderSheetId)
            logger.info("$className.$executedFunc >> completed.")
            return orderSheetId
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun updateOrderItemsPrice(items: List<OrderItemPriceInput>): List<OrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            if (items.isEmpty()) {
                logger.debug("$className.$executedFunc >> no order items to update.")
                return listOf()
            }
            val orderItems: List<OrderItem> = items.map {
                val orderItem = entityManager.getReference(OrderItem::class.java, it.id.toLong())
                orderItem.price = it.price
                orderItem
            }
            val selectedOrderItem = orderItemRepository.findById(items[0].id.toLong())
            val orderSheet = selectedOrderItem.get().orderSheet!!
            orderSheet.priceUpdatedAt = LocalDateTime.now()
            orderSheetRepository.save(orderSheet)
            logger.debug("$className.$executedFunc >> OrderSheet 'priceUpdatedAt' column is updated.")

            val updatedOrderItems = orderItemRepository.saveAll(orderItems)
            logger.info("$className.$executedFunc >> completed.")
            return updatedOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun getAllDailyOrderSheetsAggregates(wholesalerId: Long, pageable: Pageable): Page<OrderSheetsAggregate> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val monthsToSubtract: Long = 3.toLong()

            val curDate = Date()
            val dateBeforeThreeMonth = Date.from(
                LocalDate.now().minusMonths(monthsToSubtract).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
            val aggregates = orderSheetRepository.getAllDailyOrderSheetsAggregates(
                wholesalerId,
                dateBeforeThreeMonth,
                curDate,
                pageable
            )
            logger.info("$className.$executedFunc >> completed.")
            return aggregates
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun getDailyOrderSheetsAggregate(wholesalerId: Long, dateStr: String?): Optional<OrderSheetsAggregate> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            var curDate = Date()
            if (dateStr != null) {
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                curDate = formatter.parse(dateStr)
            }
            val aggregation = orderSheetRepository.getDailyOrderSheetsAggregate(wholesalerId, curDate)
            logger.info("$className.$executedFunc >> completed.")
            return if (aggregation.getDate() == null) {
                Optional.empty()
            } else {
                Optional.of(aggregation)
            }
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }

    }

    @Transactional
    fun issueOrderSheetReceipt(orderSheetId: Long): OrderSheetReceipt {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            logger.debug("$className.$executedFunc >> init")

            val orderSheet: Optional<OrderSheet> = orderSheetRepository.findById(orderSheetId)
            if (orderSheet.isEmpty) throw CustomException(
                message = "There's no OrderSheet data whose id is $orderSheetId",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_ORDER_SHEET
            )
            logger.debug("$className.$executedFunc >> orderSheet is existed.")


            val isAllNullPrice = orderSheet.get().orderItems.all { it.price == null }
            if (isAllNullPrice) {
                throw CustomException(
                    message = "Not able to issue receipt with order items in case of all items have empty price.",
                    errorType = ErrorType.INTERNAL,
                    errorCode = CustomErrorCode.NO_PRICE_ORDER_ITEM
                )
            }
            logger.debug("$className.$executedFunc >> order items price data validated.")

            val wholesalerBusinessInfo: Optional<BusinessInfo> =
                businessInfoRepository.findByUserId(orderSheet.get().wholesaler!!.id!!)
            if (wholesalerBusinessInfo.isEmpty) throw CustomException(
                message = "There's no business info data on wholesaler. Need to upload business info to wholesaler first. Not able to process receipt without business info data.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_BUSINESS_INFO
            )
            logger.debug("$className.$executedFunc >> businessInfo of wholesaler is validated.")

            val input = ReceiptProcessInput(
                orderNo = orderSheet.get().orderNo,
                retailer = ReceiptProcessInput.Retailer(name = orderSheet.get().retailer!!.name!!),
                wholesaler = ReceiptProcessInput.Wholesaler(
                    businessNo = orderSheet.get().wholesaler!!.businessInfo!!.businessNo,
                    companyName = orderSheet.get().wholesaler!!.businessInfo!!.companyName,
                    employerName = orderSheet.get().wholesaler!!.businessInfo!!.employerName,
                    sealStampImgUrl = orderSheet.get().wholesaler!!.businessInfo!!.sealStampImgUrl,
                    address = orderSheet.get().wholesaler!!.businessInfo!!.address,
                    companyPhoneNo = orderSheet.get().wholesaler!!.businessInfo!!.companyPhoneNo,
                    businessMainCategory = orderSheet.get().wholesaler!!.businessInfo!!.businessMainCategory,
                    businessSubCategory = orderSheet.get().wholesaler!!.businessInfo!!.businessSubCategory,
                    bankAccount = orderSheet.get().wholesaler!!.businessInfo!!.bankAccount,
                ),
                orderItems = orderSheet.get().orderItems.map {
                    ReceiptProcessInput.OrderItem(
                        flower = ReceiptProcessInput.Flower(
                            name = it.flower!!.name,
                            flowerType = ReceiptProcessInput.FlowerType(name = it.flower!!.flowerType!!.name),
                        ),
                        quantity = it.quantity!!,
                        price = it.price,
                        grade = it.grade!!,
                    )
                }
            )
            logger.debug("$className.$executedFunc >> receipt object is created.")

            val receiptInfo: ReceiptProcessOutput = runBlocking {
                withContext(Dispatchers.Default) { generateReceipt(input) }
            }
            logger.debug("$className.$executedFunc >> [file-process-service] processing receipt is completed. -> response : ($receiptInfo)")

            val orderSheetReceipt = OrderSheetReceipt(
                orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId),
                filePath = receiptInfo.filePath,
                fileFormat = receiptInfo.fileFormat,
                metadata = receiptInfo.metadata
            )
            val createdReceipt = orderSheetReceiptRepository.save(orderSheetReceipt)

            logger.debug("$className.$executedFunc >> OrderSheetReceipt is created.")

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
            logger.debug("$className.$executedFunc >> Sent issue receipt message.")

            return createdReceipt
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }


    private suspend fun generateReceipt(input: ReceiptProcessInput): ReceiptProcessOutput {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val client = createReceiptProcessClient()
            val output = client.post().body(BodyInserters.fromValue(input)).awaitExchange {
                onReceiptResponse(it)
            }
            logger.info("$className.$executedFunc >> completed.")
            return output
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    private fun createReceiptProcessClient(): WebClient {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val client = WebClient
                .builder()
                .baseUrl(receiptProcessHost)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
            logger.info("$className.$executedFunc >> completed.")
            return client
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    private suspend fun onReceiptResponse(res: ClientResponse): ReceiptProcessOutput {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            if (res.statusCode() == HttpStatus.OK || res.statusCode() == HttpStatus.CREATED) {
                val strData: String = res.awaitBody<String>()
                val output = Json.decodeFromString<ReceiptProcessOutput>(strData)
                logger.info("$className.$executedFunc >> completed.")
                return output
            } else {
                throw Exception(res.awaitBody<String>())
            }
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }
}