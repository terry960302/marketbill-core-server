package kr.co.marketbill.marketbillcoreserver.legacy.application.service.order

import com.netflix.graphql.types.errors.ErrorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.co.marketbill.marketbillcoreserver.shared.constants.*
import kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput
import kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.ReceiptProcessOutput
import kr.co.marketbill.marketbillcoreserver.application.service.common.MessagingService
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BusinessInfo
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.*
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.BusinessInfoRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.*
import kr.co.marketbill.marketbillcoreserver.domain.vo.DailyOrderItemKey
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException
import kr.co.marketbill.marketbillcoreserver.types.CustomOrderItemInput
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput
import kr.co.marketbill.marketbillcoreserver.shared.util.EnumConverter.Companion.convertFlowerGradeToKor
import kr.co.marketbill.marketbillcoreserver.shared.util.StringGenerator
import kr.co.marketbill.marketbillcoreserver.shared.util.groupFillBy
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
import java.util.logging.Logger
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


    @Transactional
    fun orderAllCartItems(retailerId: Long): OrderSheet {
        val (shoppingSession, cartItems) = validateCartItems(retailerId)
        closeSession(shoppingSession)
        val orderSheet = createOrderSheet(shoppingSession)
        val createdItems = createOrderItems(cartItems, orderSheet, shoppingSession.wholesaler)
        createOrderItemGroups(createdItems)
        return orderSheet
    }


    @Transactional(readOnly = true)
    fun getOrderSheets(userId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderSheet> {

        val orderSheets: Page<OrderSheet> = orderSheetRepository.findAllWithFilters(pageable, userId, role, date)
        return orderSheets
    }

    @Transactional(readOnly = true)
    fun getOrderItems(wholesalerId: Long?, role: AccountRole?, date: LocalDate?, pageable: Pageable): Page<OrderItem> {
            val orderItems = orderItemRepository.findAll(
                OrderItemSpecs.atDate(date).and(OrderItemSpecs.byUserId(wholesalerId, role)),
                pageable
            )
            return orderItems
    }

    @Transactional(readOnly = true)
    fun getDailyOrderItems(
        wholesalerId: Long?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        pageable: Pageable
    ): Page<DailyOrderItem> {
            val items = dailyOrderItemRepository.findAll(
                DailyOrderItemSpecs.byWholesalerId(wholesalerId).and(DailyOrderItemSpecs.btwDates(fromDate, toDate)),
                pageable
            )
            return items
    }


    fun getAllOrderItemsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<OrderItem>> {
            val orderItems = orderItemRepository.findAll(OrderItemSpecs.byOrderSheetIds(orderSheetIds), pageable)
            val groupedOrderItems = orderItems.groupFillBy(orderSheetIds) { it.orderSheet!!.id!! }
                .toMutableMap()
            return groupedOrderItems
    }

    fun getAllCustomOrderItemsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<CustomOrderItem>> {
            val customOrderItems =
                customOrderItemRepository.findAll(CustomOrderItemSpecs.byOrderSheetIds(orderSheetIds), pageable)
            val groupedCustomOrderItems = customOrderItems.groupFillBy(orderSheetIds) { it.orderSheet!!.id!! }
                .toMutableMap()
            return groupedCustomOrderItems
        }
    }

    fun getAllOrderSheetReceiptsByOrderSheetIds(
        orderSheetIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<OrderSheetReceipt>> {
            val orderSheetReceipts =
                orderSheetReceiptRepository.findAll(OrderSheetReceiptSpecs.byOrderSheetIds(orderSheetIds), pageable)
            val groupedOrderItems = orderSheetReceipts.groupFillBy(orderSheetIds) { it.orderSheet!!.id!! }
                .toMutableMap()
            return groupedOrderItems
    }

    fun getOrderSheet(orderSheetId: Long): OrderSheet {

            val orderSheet = orderSheetRepository.findById(orderSheetId).orElseThrow {
                CustomException(
                    message = "There's no order sheet data whose id is $orderSheetId",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_ORDER_SHEET
                )
            }
            return orderSheet
    }

    @Transactional
    fun removeOrderSheet(orderSheetId: Long): Long {

            orderSheetRepository.findById(orderSheetId).orElseThrow {
                CustomException(
                    message = "There's no order sheet data want to delete",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_ORDER_SHEET
                )
            }
            deleteOrderItemGroups(orderSheetId)
            orderSheetRepository.deleteById(orderSheetId)
            return orderSheetId

    @Transactional
    fun updateOrderItemsPrice(items: List<OrderItemPriceInput>): List<OrderItem> {

            val filteredItems = items.filter { it.price >= 0 }
            if (filteredItems.isEmpty()) {
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

            val updatedOrderItems = orderItemRepository.saveAll(orderItems)
            return updatedOrderItems
    }

    /**
     * ## 판매가 일괄적용의 가격 수정
     * : DailyOrderItem 의 가격수정이 이뤄지면, OrderItem 에도 영향
     */
    @Transactional
    fun updateDailyOrderItemsPrice(items: List<OrderItemPriceInput>): List<DailyOrderItem> {

            val filteredItems = items.filter { it.price > 0 }
            if (filteredItems.isEmpty()) {
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
            orderSheetRepository.saveAll(allConnectedOrderSheets)

            val updatedDailyOrderItems = dailyOrderItemRepository.saveAll(dailyOrderItems)
            return updatedDailyOrderItems
    }

    @Transactional(readOnly = true)
    fun getAllDailyOrderSheetsAggregates(wholesalerId: Long, pageable: Pageable): Page<kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.OrderSheetsAggregate> {

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
            return aggregates
    }

    @Transactional(readOnly = true)
    fun getDailyOrderSheetsAggregate(wholesalerId: Long, dateStr: String?): Optional<kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.OrderSheetsAggregate> {

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

    }

    @Transactional
    fun upsertCustomOrderItems(orderSheetId: Long, items: List<CustomOrderItemInput>): List<CustomOrderItem> {

            val orderSheet = orderSheetRepository.findById(orderSheetId)
            if (orderSheet.isEmpty) {
                throw CustomException(
                    message = "There's no OrderSheet data whose id is $orderSheetId",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_ORDER_SHEET
                )
            }

            val customOrderItems = items.map {
                val item = CustomOrderItem(
                    id = it.id?.toLong(),
                    orderSheet = orderSheet,
                    retailer = orderSheet.retailer,
                    wholesaler = orderSheet.wholesaler,
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
                    item.id = prevItem.map { it.id }.orElse(null)
                }
                item
            }

            val affectedCustomOrderItems = customOrderItemRepository.saveAll(customOrderItems)

            if (customOrderItems.any { it.price != null }) {
                orderSheet.priceUpdatedAt = LocalDateTime.now()
                orderSheetRepository.save(orderSheet)
            }
            return affectedCustomOrderItems
    }

    @Transactional
    fun issueOrderSheetReceipt(orderSheetId: Long): OrderSheetReceipt {


            val orderSheet = orderSheetRepository.findById(orderSheetId).orElseThrow {
                CustomException(
                    message = "There's no OrderSheet data whose id is $orderSheetId",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_ORDER_SHEET
                )
            }

            val orderItems: List<OrderItem> = orderSheet.orderItems
            val customOrderItems: List<CustomOrderItem> = orderSheet.customOrderItems.filter {
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
                    errorCode = ErrorCode.NO_PRICE_ORDER_ITEM
                )
            }

            val wholesalerBusinessInfo =
                businessInfoRepository.findByUserId(orderSheet.wholesaler!!.id!!).orElseThrow {
                    CustomException(
                        message = "There's no business info data on wholesaler. Need to upload business info to wholesaler first. Not able to process receipt without business info data.",
                        errorType = ErrorType.NOT_FOUND,
                        errorCode = ErrorCode.NO_BUSINESS_INFO
                    )
                }

            val orderItemsInput = orderItems.map {
                kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.OrderItem(
                    flower = kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.Flower(
                        name = it.flower!!.name,
                        flowerType = kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.FlowerType(name = it.flower!!.flowerType!!.name),
                    ),
                    quantity = it.quantity!!,
                    price = it.price,
                    grade = it.grade!!,
                )
            }
            val customOrderItemsInput = customOrderItems.map {
                kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.OrderItem(
                    flower = kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.Flower(
                        name = it.flowerName!!,
                        flowerType = kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.FlowerType(name = it.flowerTypeName!!),
                    ),
                    quantity = it.quantity!!,
                    price = it.price,
                    grade = it.grade!!,
                )
            }

            val input = kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput(
                orderNo = orderSheet.orderNo,
                retailer = kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.Retailer(
                    name = orderSheet.retailer!!.name!!
                ),
                wholesaler = kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.Wholesaler(
                    businessNo = orderSheet.wholesaler!!.businessInfo!!.businessNo,
                    companyName = orderSheet.wholesaler!!.businessInfo!!.companyName,
                    employerName = orderSheet.wholesaler!!.businessInfo!!.employerName,
                    sealStampImgUrl = orderSheet.wholesaler!!.businessInfo!!.sealStampImgUrl,
                    address = orderSheet.wholesaler!!.businessInfo!!.address,
                    companyPhoneNo = orderSheet.wholesaler!!.businessInfo!!.companyPhoneNo,
                    businessMainCategory = orderSheet.wholesaler!!.businessInfo!!.businessMainCategory,
                    businessSubCategory = orderSheet.wholesaler!!.businessInfo!!.businessSubCategory,
                    bankAccount = orderSheet.wholesaler!!.businessInfo!!.bankAccount,
                ),
                orderItems = orderItemsInput + customOrderItemsInput
            )


            val receiptInfo: kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.ReceiptProcessOutput = runBlocking {
                withContext(Dispatchers.IO) { generateReceipt(input) }
            }

            val orderSheetReceipt = OrderSheetReceipt(
                orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId),
                fileName = receiptInfo.fileName,
                filePath = receiptInfo.filePath,
                fileFormat = receiptInfo.fileFormat,
                metadata = receiptInfo.metadata
            )
            val createdReceipt = orderSheetReceiptRepository.save(orderSheetReceipt)


            val targetPhoneNo = orderSheet.retailer!!.userCredential!!.phoneNo
            val wholesalerName = orderSheet.wholesaler!!.name!!
            val orderNo = orderSheet.orderNo

            runBlocking {
                messagingService.sendIssueOrderSheetReceiptSMS(
                    to = targetPhoneNo,
                    wholesalerName = wholesalerName,
                    orderNo = orderNo,
                )
            }
            return createdReceipt
    }

    private fun validateCartItems(retailerId: Long): Pair<ShoppingSession, List<CartItem>> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val shoppingSession = shoppingSessionRepository
                .findOne(ShoppingSessionSpecs.byRetailerId(retailerId))
                .orElseThrow {
                    CustomException(
                        message = "There's no shopping_session whose retailerID is $retailerId.",
                        errorType = ErrorType.NOT_FOUND,
                        errorCode = ErrorCode.NO_SHOPPING_SESSION
                    )
                }
            val cartItems = cartItemRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
                .map {
                    it.orderedAt = LocalDateTime.now()
                    it
                }.content

            if (cartItems.isEmpty()) throw CustomException(
                message = "There's no cart items to order.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.NO_CART_ITEM
            )

            val isAllConnectedWithWholesaler =
                cartItems.mapNotNull { it.wholesaler }.size == cartItems.size && shoppingSession.wholesaler != null
            if (!isAllConnectedWithWholesaler) throw CustomException(
                message = "There's no connected wholesaler on cart items.",
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.NO_CART_WHOLESALER
            )
            logger.info("$className.$executedFunc >> All cart items have wholesaler info.")

            cartItemRepository.saveAll(cartItems)
            cartItemRepository.flush()
            logger.info("$className.$executedFunc >> All cart items are ordered.")

            return Pair(shoppingSession, cartItems)
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    private fun closeSession(shoppingSession: ShoppingSession) {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            shoppingSessionRepository.delete(shoppingSession)
            logger.info("$className.$executedFunc >> Shopping_session of retailer(${shoppingSession.retailer?.id}) is deleted(closed).")
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    private fun createOrderSheet(shoppingSession: ShoppingSession): OrderSheet {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderSheet = OrderSheet(
                orderNo = "",
                retailer = shoppingSession.retailer,
                wholesaler = shoppingSession.wholesaler,
                memo = shoppingSession.memo,
            )
            val savedOrderSheet = orderSheetRepository.save(orderSheet)
            savedOrderSheet.orderNo = StringGenerator.generateOrderNo(savedOrderSheet.id!!)
            val updatedOrderSheet = orderSheetRepository.save(savedOrderSheet)
            logger.info("$className.$executedFunc >> OrderSheet is created.")
            return updatedOrderSheet
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    private fun createOrderItems(
        cartItems: List<CartItem>,
        orderSheet: OrderSheet,
        wholesaler: User?
    ): List<OrderItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val orderItems = cartItems.map {
                OrderItem(
                    retailer = it.retailer,
                    orderSheet = orderSheet,
                    wholesaler = wholesaler,
                    flower = it.flower,
                    quantity = it.quantity,
                    grade = it.grade,
                    price = null,
                )
            }
            val createdOrderItems = orderItemRepository.saveAll(orderItems)
            logger.info("$className.$executedFunc >> Order items are created by cart items.")
            return createdOrderItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun createOrderItemGroups(orderItems: List<OrderItem>): List<DailyOrderItem> {

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

            return dailyOrderItemRepository.saveAll(newDailyOrderItems)
    }

    @Transactional
    private fun deleteOrderItemGroups(orderSheetId: Long) {

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

            return dailyOrderItemRepository.deleteAllById(orderItemGroupIdsToDelete)
    }

    private suspend fun generateReceipt(input: kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput): kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.ReceiptProcessOutput {

            val client = createReceiptProcessClient()
            val output = client.post().body(BodyInserters.fromValue(input)).awaitExchange {
                onReceiptResponse(it)
            }
            return output
    }

    private fun createReceiptProcessClient(): WebClient {

            val client = WebClient
                .builder()
                .baseUrl(receiptProcessHost)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
            return client
        }
    }

    private suspend fun onReceiptResponse(res: ClientResponse): kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.ReceiptProcessOutput {

            if (res.statusCode() == HttpStatus.OK || res.statusCode() == HttpStatus.CREATED) {
                val strData: String = res.awaitBody<String>()
                val output = Json.decodeFromString<kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.ReceiptProcessOutput>(strData)
                return output
                throw Exception(res.awaitBody<String>())
            }
        }
    }
}