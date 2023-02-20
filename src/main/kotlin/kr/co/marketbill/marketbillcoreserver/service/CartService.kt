package kr.co.marketbill.marketbillcoreserver.service

import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.*
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.UserRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.CartItemSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.ShoppingSessionSpecs
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter.Companion.convertFlowerGradeToKor
import kr.co.marketbill.marketbillcoreserver.util.StringGenerator
import kr.co.marketbill.marketbillcoreserver.util.groupFillBy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityManager

@Service
class CartService {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    @Autowired
    private lateinit var shoppingSessionRepository: ShoppingSessionRepository

    @Autowired
    private lateinit var orderSheetRepository: OrderSheetRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var batchCartToOrderLogsRepository: BatchCartToOrderLogsRepository

    private val logger: Logger = LoggerFactory.getLogger(CartService::class.java)
    private val className = this.javaClass.simpleName

    @Transactional(readOnly = true)
    fun getShoppingSession(retailerId: Long): Optional<ShoppingSession> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            return shoppingSessionRepository.findOne(ShoppingSessionSpecs.byRetailerId(retailerId))
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun addCartItem(retailerId: Long, flowerId: Long, quantity: Int, grade: FlowerGrade): CartItem {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val shoppingSession: Optional<ShoppingSession> =
                shoppingSessionRepository.findOne(ShoppingSessionSpecs.byRetailerId(retailerId))

            val session: ShoppingSession = if (shoppingSession.isEmpty) {
                val newSession = ShoppingSession(
                    retailer = entityManager.getReference(User::class.java, retailerId),
                    wholesaler = null,
                    memo = null,
                )
                shoppingSessionRepository.save(newSession)
            } else {
                shoppingSession.get()
            }

            // 꽃, 등급, 소매상, 세션이 모두 동일하면 수량만 바뀌므로 업데이트처리(그외에 소매상ID가 다르거나 품질이 다르거나 꽃이 다르면 새로 장바구니에 추가)
            // retailer_id, flower_id, grade, session_id 복수 컬럼에 unique 처리했기에 findOne 사용가능
            val prevCartItem: Optional<CartItem> =
                cartItemRepository.findOne(
                    CartItemSpecs
                        .byRetailerId(retailerId)
                        .and(CartItemSpecs.byFlowerId(flowerId))
                        .and(CartItemSpecs.byFlowerGrade(convertFlowerGradeToKor(grade)))
                        .and(CartItemSpecs.byShoppingSessionId(session.id))
                )

            val newCartItem = CartItem(
                retailer = entityManager.getReference(User::class.java, retailerId),
                wholesaler = session.wholesaler,
                flower = entityManager.getReference(Flower::class.java, flowerId),
                quantity = quantity,
                shoppingSession = session,
                grade = convertFlowerGradeToKor(grade)
            )

            if (prevCartItem.isPresent) {
                newCartItem.id = prevCartItem.get().id
            }

            val createdCartItem = cartItemRepository.save(newCartItem)

            logger.info("$className.$executedFunc >> completed.")
            return createdCartItem
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun updateCartItem(id: Long, quantity: Int, grade: FlowerGrade): CartItem {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val cartItem = cartItemRepository.findById(id)
            if (cartItem.isEmpty) throw CustomException(
                message = "There's no cart_item whose ID is $id",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_CART_ITEM
            )
            val item = cartItem.get()
            logger.info("$className.$executedFunc >> cart_item is existed.")

            val sameCartItem: Optional<CartItem> = cartItemRepository.findOne(
                CartItemSpecs.excludeId(id).and(
                    CartItemSpecs.byRetailerId(item.retailer!!.id)
                ).and(
                    CartItemSpecs.byFlowerId(item.flower!!.id)
                ).and(
                    CartItemSpecs.byFlowerGrade(convertFlowerGradeToKor(grade))
                ).and(
                    CartItemSpecs.byShoppingSessionId(item.shoppingSession!!.id)
                )
            )

            if (sameCartItem.isPresent) {
                item.quantity = sameCartItem.get().quantity!! + quantity
                cartItemRepository.deleteById(sameCartItem.get().id!!)
                cartItemRepository.flush()
            } else {
                item.quantity = quantity
            }
            item.grade = convertFlowerGradeToKor(grade)

            val updatedCartItem = cartItemRepository.save(item)
            logger.info("$className.$executedFunc >> completed.")
            return updatedCartItem
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun removeCartItem(cartItemId: Long): Long {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val cartItem: Optional<CartItem> = cartItemRepository.findById(cartItemId)
            if (cartItem.isEmpty) {
                throw CustomException(
                    message = "There's no cart item data want to delete.",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_CART_ITEM
                )
            }
            val session: ShoppingSession = cartItem.get().shoppingSession!!
            val allCartItemsInSession: List<CartItem> =
                cartItemRepository.findAll(CartItemSpecs.byShoppingSessionId(session.id))

            if (allCartItemsInSession.size == 1) {
                shoppingSessionRepository.delete(session)
            } else {
                cartItemRepository.delete(cartItem.get())
            }
            logger.info("$className.$executedFunc >> completed.")
            return cartItemId
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun updateShoppingSession(retailerId: Long, wholesalerId: Long?, memo: String?): ShoppingSession {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            if (wholesalerId == null && memo == null) {
                throw CustomException(
                    message = "There's no input data to update.",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.INVALID_DATA
                )
            }

            val shoppingSession: Optional<ShoppingSession> =
                shoppingSessionRepository.findOne(ShoppingSessionSpecs.byRetailerId(retailerId))
            if (shoppingSession.isEmpty) {
                throw CustomException(
                    message = "There's no shopping session whose retailerID is $retailerId",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_SHOPPING_SESSION
                )
            }
            val session = shoppingSession.get()

            if (wholesalerId != null) {
                val wholesaler = userRepository.findById(wholesalerId)
                if (wholesaler.isEmpty) {
                    throw CustomException(
                        message = "There's no wholesaler whose ID is $wholesalerId",
                        errorType = ErrorType.NOT_FOUND,
                        errorCode = CustomErrorCode.NO_USER
                    )
                }
                logger.info("$className.$executedFunc >> wholesaler both are exists.")

                session.wholesaler = wholesaler.get()
                shoppingSessionRepository.save(session)
                logger.info("$className.$executedFunc >> wholesaler data on shopping_session updated.")

                val cartItems = cartItemRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
                val updatedCartItemObjs = cartItems.map {
                    it.wholesaler = entityManager.getReference(User::class.java, wholesalerId)
                    it
                }
                cartItemRepository.saveAll(updatedCartItemObjs)
                logger.info("$className.$executedFunc >> wholesaler data on cart_items updated.")
            }

            if (memo != null) {
                session.memo = memo
                shoppingSessionRepository.save(session)
                logger.info("$className.$executedFunc >> memo data on shopping_session updated.")
            }

            logger.info("$className.$executedFunc >> completed.")
            return session

        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    /**
     * ### 스케쥴러
     * : 매일 오후 10시에 자동 주문처리
     */
    @Scheduled(cron = "0 0 22 * * ?", zone = "Asia/Seoul")
    @Transactional
    fun orderBatchCartItems() {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        val newOrderSheets = mutableListOf<OrderSheet>()
        val newOrderItems = mutableListOf<OrderItem>()

        val log = BatchCartToOrderLogs(
            cartItemsCount = 0,
            orderSheetCount = -1,
            orderItemCount = -1,
            errLogs = ""
        )

        try {
            val validCartItems =
                cartItemRepository.findAll(CartItemSpecs.hasWholesaler()).filter { it.wholesaler != null }
            log.cartItemsCount = validCartItems.size
            logger.info("$className.$executedFunc >> fetched all cart items each has connected wholesaler info.")

            val cartItemGroup: Map<Long, List<CartItem>> = validCartItems.groupBy { it.retailer!!.id!! }

            cartItemGroup.forEach { (retailerId, cartItems) ->
                val retailer = entityManager.getReference(User::class.java, retailerId)
                val shoppingSession = shoppingSessionRepository.findOne(ShoppingSessionSpecs.byRetailerId(retailerId))
                val wholesaler = shoppingSession.get().wholesaler

                val orderedAt = LocalDateTime.now()
                cartItemRepository.saveAll(
                    cartItems.map {
                        it.orderedAt = orderedAt
                        it
                    }
                )
                logger.info("$className.$executedFunc >> cart items are ordered. (${cartItems.map { it.id }})")
                shoppingSessionRepository.delete(shoppingSession.get())
                logger.info("$className.$executedFunc >> shopping session is deleted(closed)")

                val orderSheet = OrderSheet(
                    orderNo = "",
                    retailer = retailer,
                    wholesaler = wholesaler,
                )
                newOrderSheets.add(orderSheet)
                val savedOrderSheet = orderSheetRepository.save(orderSheet)
                savedOrderSheet.orderNo = StringGenerator.generateOrderNo(savedOrderSheet.id!!)
                val updatedOrderSheet = orderSheetRepository.save(savedOrderSheet)
                logger.info("$className.$executedFunc >> OrderSheet(ID:${updatedOrderSheet.id}) is created.")

                val orderItems = cartItems.map {
                    OrderItem(
                        orderSheet = updatedOrderSheet,
                        retailer = retailer,
                        wholesaler = wholesaler,
                        flower = it.flower,
                        quantity = it.quantity,
                        grade = it.grade,
                        price = null,
                    )
                }
                newOrderItems.addAll(orderItems)
                orderItemRepository.saveAll(orderItems)
                logger.info("$className.$executedFunc >> OrderItems(IDs:${orderItems.map { it.id }}) is created.")
            }

            log.orderSheetCount = newOrderSheets.size
            log.orderItemCount = newOrderItems.size
            logger.info("$className.$executedFunc >> done(no issue).")
        } catch (e: java.lang.Exception) {
            log.errLogs = e.message
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        } finally {
            batchCartToOrderLogsRepository.save(log)
            logger.info("$className.$executedFunc >> completed.")
        }
    }


    fun getAllCartItemsByShoppingSessionIds(
        shoppingSessionIds: List<Long>,
        pageable: Pageable
    ): MutableMap<Long, List<CartItem>> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val cartItems = cartItemRepository.findAll(CartItemSpecs.byShoppingSessionIds(shoppingSessionIds), pageable)
            val groupedCartItems = cartItems.groupFillBy(shoppingSessionIds) { it.shoppingSession!!.id!! }
                .toMutableMap()
            logger.info("$className.$executedFunc >> completed.")
            return groupedCartItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun getCartItemsByShoppingSessionId(
        shoppingSessionId: Long,
        pageable: Pageable
    ): Page<CartItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val cartItems = cartItemRepository.findAll(CartItemSpecs.byShoppingSessionId(shoppingSessionId), pageable)
            logger.info("$className.$executedFunc >> completed.")
            return cartItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }
}