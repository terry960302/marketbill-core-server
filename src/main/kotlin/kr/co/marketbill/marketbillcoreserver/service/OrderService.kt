package kr.co.marketbill.marketbillcoreserver.service

import com.netflix.graphql.types.errors.ErrorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.co.marketbill.marketbillcoreserver.constants.*
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.domain.dto.ReceiptProcessInput
import kr.co.marketbill.marketbillcoreserver.domain.dto.ReceiptProcessOutput
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BusinessInfo
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.*
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.BusinessInfoRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.*
import kr.co.marketbill.marketbillcoreserver.domain.vo.DailyOrderItemKey
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.types.CustomOrderItemInput
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter.Companion.convertFlowerGradeToKor
import kr.co.marketbill.marketbillcoreserver.util.StringGenerator
import kr.co.marketbill.marketbillcoreserver.util.groupFillBy
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
    private lateinit var cartItemRepository: CartItemRepository

    @Autowired
    private lateinit var shoppingSessionRepository: ShoppingSessionRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var customOrderItemRepository: CustomOrderItemRepository

    @Autowired
    private lateinit var dailyOrderItemRepository: DailyOrderItemRepository

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
    fun orderAllCartItems(retailerId: Long): OrderSheet {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val shoppingSession: Optional<ShoppingSession> =
                shoppingSessionRepository.findOne(ShoppingSessionSpecs.byRetailerId(retailerId))
            val cartItems = cartItemRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
                .map {
                    it.orderedAt = LocalDateTime.now()
                    it
                }.get().toList()

            if (shoppingSession.isEmpty) {
                throw CustomException(
                    message = "There's no shopping_session whose retailerID is $retailerId.",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_SHOPPING_SESSION
                )

            }

            if (cartItems.isEmpty()) throw CustomException(
                message = "There's no cart items to order.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_CART_ITEM
            )

            val isAllConnectedWithWholesaler =
                cartItems.mapNotNull { it.wholesaler }.size == cartItems.size && shoppingSession.get().wholesaler != null
            if (!isAllConnectedWithWholesaler) throw CustomException(
                message = "There's no connected wholesaler on cart items.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_CART_WHOLESALER
            )
            logger.info("$className.$executedFunc >> All cart items have wholesaler info.")

            cartItemRepository.saveAll(cartItems)
            cartItemRepository.flush()
            logger.info("$className.$executedFunc >> All cart items are ordered.")

            shoppingSessionRepository.delete(shoppingSession.get())
            logger.info("$className.$executedFunc >> Shopping_session of retailer($retailerId) is deleted(closed).")

            val orderSheet = OrderSheet(
                orderNo = "",
                retailer = shoppingSession.get().retailer,
                wholesaler = shoppingSession.get().wholesaler,
                memo = shoppingSession.get().memo,
            )
            val savedOrderSheet = orderSheetRepository.save(orderSheet)
            logger.info("$className.$executedFunc >> OrderSheet is created.")
            savedOrderSheet.orderNo = StringGenerator.generateOrderNo(savedOrderSheet.id!!)
            val updatedOrderSheet = orderSheetRepository.save(savedOrderSheet)

            val orderItems = cartItems.map {
                OrderItem(
                    retailer = it.retailer,
                    orderSheet = updatedOrderSheet,
                    wholesaler = shoppingSession.get().wholesaler,
                    flower = it.flower,
                    quantity = it.quantity,
                    grade = it.grade,
                    price = null,
                )
            }
            val createdOrderItems: List<OrderItem> = orderItemRepository.saveAll(orderItems)
            logger.info("$className.$executedFunc >> Order items are created by cart items.")
            createOrderItemGroups(createdOrderItems)
            logger.info("$className.$executedFunc >> completed.")
            return updatedOrderSheet
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }


    @Transactional(readOnly = true)
    fun getOrderSheets(userId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderSheet> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderSheets : Page<OrderSheet> = orderSheetRepository.findAllWithFilters(pageable, userId, role, date)
            logger.info("$className.$executedFunc >> completed.")
            return orderSheets
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    fun getDailyOrderItems(
        wholesalerId: Long?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        pageable: Pageable
    ): Page<DailyOrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val items = dailyOrderItemRepository.findAll(
                DailyOrderItemSpecs.byWholesalerId(wholesalerId).and(DailyOrderItemSpecs.btwDates(fromDate, toDate)),
                pageable
            )
            logger.info("$className.$executedFunc >> completed.")
            return items
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
            val groupedOrderItems = orderItems.groupFillBy(orderSheetIds) { it.orderSheet!!.id!! }
                .toMutableMap()
            logger.info("$className.$executedFunc >> completed.")
            return groupedOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun getAllCustomOrderItemsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<CustomOrderItem>> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val customOrderItems =
                customOrderItemRepository.findAll(CustomOrderItemSpecs.byOrderSheetIds(orderSheetIds), pageable)
            val groupedCustomOrderItems = customOrderItems.groupFillBy(orderSheetIds) { it.orderSheet!!.id!! }
                .toMutableMap()
            logger.info("$className.$executedFunc >> completed.")
            return groupedCustomOrderItems
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
            val groupedOrderItems = orderSheetReceipts.groupFillBy(orderSheetIds) { it.orderSheet!!.id!! }
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

    @Transactional
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
            deleteOrderItemGroups(orderSheetId)
            orderSheetRepository.deleteById(orderSheetId)
            logger.info("$className.$executedFunc >> completed.")
            return orderSheetId
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun updateOrderItemsPrice(items: List<OrderItemPriceInput>): List<OrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val filteredItems = items.filter { it.price >= 0 }
            if (filteredItems.isEmpty()) {
                logger.info("$className.$executedFunc >> no order items to update.")
                return listOf()
            }

            val orderItems: List<OrderItem> = filteredItems.map {
                val orderItem = entityManager.getReference(OrderItem::class.java, it.id.toLong())
                orderItem.price = it.price
                orderItem
            }
            val selectedOrderItem: OrderItem = orderItems[0]
            val orderSheet = selectedOrderItem.orderSheet!!
            orderSheet.priceUpdatedAt = LocalDateTime.now()
            orderSheetRepository.save(orderSheet)
            logger.info("$className.$executedFunc >> OrderSheet 'priceUpdatedAt' column is updated.")

            val updatedOrderItems = orderItemRepository.saveAll(orderItems)
            logger.info("$className.$executedFunc >> completed.")
            return updatedOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    /**
     * ## 판매가 일괄적용의 가격 수정
     * : DailyOrderItem 의 가격수정이 이뤄지면, OrderItem 에도 영향
     */
    @Transactional
    fun updateDailyOrderItemsPrice(items: List<OrderItemPriceInput>): List<DailyOrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val filteredItems = items.filter { it.price > 0 }
            if (filteredItems.isEmpty()) {
                logger.info("$className.$executedFunc >> No daily order items to update.")
                return listOf()
            }
            val dailyOrderItems: List<DailyOrderItem> = filteredItems.map {
                val dailyOrderItem = entityManager.getReference(DailyOrderItem::class.java, it.id.toLong())
                dailyOrderItem.price = it.price
                dailyOrderItem
            }

            val allConnectedOrderItems: List<OrderItem> = dailyOrderItems.flatMap { parent ->
                val connectedOrderItems: List<OrderItem> = orderItemRepository.findAll(
                    OrderItemSpecs.byItemKey(
                        DailyOrderItemKey(
                            flowerId = parent.flower!!.id!!,
                            wholesalerId = parent.wholesaler!!.id!!,
                            grade = parent.grade!!,
                            date = parent.createdAt.toLocalDate()
                        )
                    )
                )
                connectedOrderItems.map { child ->
                    child.price = parent.price
                    child
                }
            }.toList()

            val allConnectedOrderSheets: List<OrderSheet> = allConnectedOrderItems.map { it.orderSheet!! }.map {
                it.priceUpdatedAt = LocalDateTime.now()
                it
            }

            orderItemRepository.saveAll(allConnectedOrderItems)
            logger.info("$className.$executedFunc >> OrderItem price is updated.")
            orderSheetRepository.saveAll(allConnectedOrderSheets)
            logger.info("$className.$executedFunc >> OrderSheet 'priceUpdatedAt' column is updated.")

            val updatedDailyOrderItems = dailyOrderItemRepository.saveAll(dailyOrderItems)
            logger.info("$className.$executedFunc >> completed.")
            return updatedDailyOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
    fun upsertCustomOrderItems(orderSheetId: Long, items: List<CustomOrderItemInput>): List<CustomOrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderSheet = orderSheetRepository.findById(orderSheetId)
            if (orderSheet.isEmpty) {
                throw CustomException(
                    message = "There's no OrderSheet data whose id is $orderSheetId",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_ORDER_SHEET
                )
            }

            val customOrderItems = items.map {
                val item = CustomOrderItem(
                    id = it.id?.toLong(),
                    orderSheet = orderSheet.get(),
                    retailer = orderSheet.get().retailer,
                    wholesaler = orderSheet.get().wholesaler,
                    flowerName = it.flowerName?.trim(),
                    flowerTypeName = it.flowerTypeName?.trim(),
                    grade = if (it.grade != null) convertFlowerGradeToKor(FlowerGrade.valueOf(it.grade.toString())) else null,
                    quantity = it.quantity,
                    price = it.price,
                )
                if (!item.flowerName.isNullOrBlank() && !item.flowerTypeName.isNullOrBlank() && item.grade != null) {
                    val prevItem = customOrderItemRepository.findOne(
                        CustomOrderItemSpecs.byOrderSheetId(orderSheetId)
                            .and(CustomOrderItemSpecs.byFlowerName(item.flowerName))
                            .and(CustomOrderItemSpecs.byFlowerTypeName(item.flowerTypeName))
                            .and(CustomOrderItemSpecs.byFlowerGrade(item.grade))
                    )
                    item.id = if (prevItem.isEmpty) null else prevItem.get().id
                }
                item
            }

            val affectedCustomOrderItems = customOrderItemRepository.saveAll(customOrderItems)

            if (customOrderItems.any { it.price != null }) {
                orderSheet.get()
                    .priceUpdatedAt = LocalDateTime.now()
                orderSheetRepository.save(orderSheet.get())
            }
            logger.info("$className.$executedFunc >> completed.")
            return affectedCustomOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun issueOrderSheetReceipt(orderSheetId: Long): OrderSheetReceipt {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            logger.info("$className.$executedFunc >> init")

            val orderSheet: Optional<OrderSheet> = orderSheetRepository.findById(orderSheetId)
            if (orderSheet.isEmpty) throw CustomException(
                message = "There's no OrderSheet data whose id is $orderSheetId",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_ORDER_SHEET
            )
            logger.info("$className.$executedFunc >> orderSheet is existed.")


            val orderItems: List<OrderItem> = orderSheet.get().orderItems
            val customOrderItems: List<CustomOrderItem> = orderSheet.get().customOrderItems.filter {
                !it.flowerTypeName.isNullOrBlank() &&
                        !it.flowerName.isNullOrBlank() &&
                        it.grade != null &&
                        it.quantity != null
            }

            val isAllNullPrice =
                orderItems.all { it.price == null } && customOrderItems.all { it.price == null }
            val isAllZeroMinusPrice =
                orderItems.all { it.price != null && it.price!! <= 0 } && customOrderItems.all { it.price != null && it.price!! <= 0 }
            if (isAllNullPrice || isAllZeroMinusPrice) {
                throw CustomException(
                    message = "Not able to issue receipt with order items in case of all items have empty price(or zero/minus price).",
                    errorType = ErrorType.INTERNAL,
                    errorCode = CustomErrorCode.NO_PRICE_ORDER_ITEM
                )
            }
            logger.info("$className.$executedFunc >> order items price data validated.")

            val wholesalerBusinessInfo: Optional<BusinessInfo> =
                businessInfoRepository.findByUserId(orderSheet.get().wholesaler!!.id!!)
            if (wholesalerBusinessInfo.isEmpty) throw CustomException(
                message = "There's no business info data on wholesaler. Need to upload business info to wholesaler first. Not able to process receipt without business info data.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_BUSINESS_INFO
            )
            logger.info("$className.$executedFunc >> businessInfo of wholesaler is validated.")

            val orderItemsInput = orderItems.map {
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
            val customOrderItemsInput = customOrderItems.map {
                ReceiptProcessInput.OrderItem(
                    flower = ReceiptProcessInput.Flower(
                        name = it.flowerName!!,
                        flowerType = ReceiptProcessInput.FlowerType(name = it.flowerTypeName!!),
                    ),
                    quantity = it.quantity!!,
                    price = it.price,
                    grade = it.grade!!,
                )
            }

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
                orderItems = orderItemsInput + customOrderItemsInput
            )

            logger.info("$className.$executedFunc >> receipt object is created.")

            val receiptInfo: ReceiptProcessOutput = runBlocking {
                withContext(Dispatchers.IO) { generateReceipt(input) }
            }
            logger.info("$className.$executedFunc >> [file-process-service] processing receipt is completed. -> response : ($receiptInfo)")

            val orderSheetReceipt = OrderSheetReceipt(
                orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId),
                fileName = receiptInfo.fileName,
                filePath = receiptInfo.filePath,
                fileFormat = receiptInfo.fileFormat,
                metadata = receiptInfo.metadata
            )
            val createdReceipt = orderSheetReceiptRepository.save(orderSheetReceipt)

            logger.info("$className.$executedFunc >> OrderSheetReceipt is created.")

            val targetPhoneNo = orderSheet.get().retailer!!.userCredential!!.phoneNo
            val wholesalerName = orderSheet.get().wholesaler!!.name!!
            val orderNo = orderSheet.get().orderNo

            runBlocking {
                messagingService.sendIssueOrderSheetReceiptSMS(
                    to = targetPhoneNo,
                    wholesalerName = wholesalerName,
                    orderNo = orderNo,
                )
            }
            logger.info("$className.$executedFunc >> Sent issue receipt message.")
            logger.info("$className.$executedFunc >> completed.")
            return createdReceipt
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun createOrderItemGroups(orderItems: List<OrderItem>): List<DailyOrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val newDailyOrderItems: List<DailyOrderItem> = orderItems.filter {
                val isAlreadyExists: Boolean = dailyOrderItemRepository.exists(
                    DailyOrderItemSpecs.byItemKey(
                        DailyOrderItemKey(
                            wholesalerId = it.wholesaler!!.id!!,
                            flowerId = it.flower!!.id!!,
                            grade = it.grade!!,
                            date = it.createdAt.toLocalDate(),
                        )
                    )
                )
                !isAlreadyExists
            }.map {
                DailyOrderItem(
                    wholesaler = it.wholesaler,
                    flower = it.flower,
                    grade = it.grade,
                )
            }
            logger.info("$className.$executedFunc >> completed.")

            return dailyOrderItemRepository.saveAll(newDailyOrderItems)
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    private fun deleteOrderItemGroups(orderSheetId: Long) {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderItems: List<OrderItem> =
                orderItemRepository.findAll(OrderItemSpecs.byOrderSheetId(orderSheetId))


            val orderItemGroupIdsToDelete: List<Long> = orderItems.flatMap {
                val groupKey = DailyOrderItemKey(
                    wholesalerId = it.wholesaler!!.id!!,
                    flowerId = it.flower!!.id!!,
                    grade = it.grade!!,
                    date = it.createdAt.toLocalDate()
                )
                val sameOrderItems = orderItemRepository.findAll(
                    OrderItemSpecs.byItemKey(groupKey).and(OrderItemSpecs.excludeId(it.id))
                )
                if (sameOrderItems.isEmpty()) {
                    dailyOrderItemRepository.findAll(DailyOrderItemSpecs.byItemKey(groupKey)).map { it.id!! }
                } else {
                    listOf()
                }
            }
            logger.info("$className.$executedFunc >> completed.")

            return dailyOrderItemRepository.deleteAllById(orderItemGroupIdsToDelete)
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
                logger.error("$className.$executedFunc >> invalid status code.")
                throw Exception(res.awaitBody<String>())
            }
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }
}