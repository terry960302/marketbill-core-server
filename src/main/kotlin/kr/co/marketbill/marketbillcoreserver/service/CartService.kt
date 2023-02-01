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
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter.Companion.convertFlowerGradeToKor
import kr.co.marketbill.marketbillcoreserver.util.StringGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import kotlin.collections.ArrayList

@Service
class CartService {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var orderSheetRepository: OrderSheetRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var batchCartToOrderLogsRepository: BatchCartToOrderLogsRepository

    private val logger: Logger = LoggerFactory.getLogger(CartService::class.java)
    private val className = this.javaClass.simpleName

//    @Transactional
//    @PostConstruct
//    fun fillCartIdOnCartItems() {
//        val cartItems = cartItemRepository.findAll(PageRequest.of(0, 999999, Sort.by("createdAt").ascending())).get().toList()
//        println(cartItems.map { it.id })
//
//        val cartItemsMap = mutableMapOf<Long, List<CartItem>>()
//        var group: CartItem = cartItems[0]
//        var cartId: Long = 1
//        cartItemsMap[cartId] = arrayListOf()
//
//        cartItems.forEach {
//            if (group?.retailer == it.retailer && group?.wholesaler == it.wholesaler) {
//                val arr = cartItemsMap[cartId] as java.util.ArrayList<CartItem>
//                arr.add(it)
//                cartItemsMap[cartId] = arr
//            } else {
//                group = it
//                cartId +=1
//                cartItemsMap[cartId] = arrayListOf(group)
//            }
//        }
//
//        cartItemsMap.forEach { cartId, cartItems ->
////            println("cartId : $t")
////            println("cartItems : ${u.map { it.id }}")
////            println("----")
//
//            val cart = cartRepository.save(Cart(
//                id = cartId,
//                retailer = cartItems[0].retailer,
//                wholesaler = cartItems[0].wholesaler,
//                memo = "",
//            ))
//
//            val newItems = cartItems.map {
//                it.cart = cart
//                it
//            }
//            cartItemRepository.saveAll(newItems)
//
//            if(cartItems[0].orderedAt != null){
//                cart.orderedAt = cartItems[0].orderedAt
//            }
//            cartRepository.save(cart)
//            if(cartItems[0].deletedAt != null){
//                cartRepository.delete(cart)
//            }
//        }
//        println("processed!!")
//    }

    @Transactional
    fun getConnectedWholesalerOnCartItems(retailerId: Long): Optional<User> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val cartItems = this.getAllCartItems(retailerId, PageRequest.of(DEFAULT_PAGE, 1))
            val connectedWholesalers = cartItems.map { it.wholesaler }.filterNotNull()
            val wholesaler = if (connectedWholesalers.isEmpty()) {
                Optional.empty()
            } else {
                Optional.of(connectedWholesalers[0])
            }
            logger.info("$className.$executedFunc >> completed.")
            return wholesaler
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }


    @Transactional(readOnly = true)
    fun getAllCartItems(userId: Long, pageable: Pageable): Page<CartItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val cartItems = cartItemRepository.findAllByRetailerId(userId, pageable)
            logger.info("$className.$executedFunc >> completed.")
            return cartItems
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }

    }

    @Transactional
    fun addToCart(userId: Long, flowerId: Long, quantity: Int, grade: FlowerGrade): CartItem {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val cartItem = CartItem(
                retailer = entityManager.getReference(User::class.java, userId),
                flower = entityManager.getReference(Flower::class.java, flowerId),
                quantity = quantity,
                grade = convertFlowerGradeToKor(grade)
            )
            // 꽃, 품질, 소매상ID가 동일하면 수량만 바뀌므로 업데이트처리(그외에 소매상ID가 다르거나 품질이 다르거나 꽃이 다르면 새로 장바구니에 추가)
            val prevCartItem: Optional<CartItem> =
                cartItemRepository.findOne(
                    CartItemSpecs.byRetailerId(userId)
                        .and(CartItemSpecs.byFlowerId(flowerId))
                        .and(
                            CartItemSpecs.byFlowerGrade(convertFlowerGradeToKor(grade))
                        )
                )

            val connectedWholesaler: Optional<User> = this.getConnectedWholesalerOnCartItems(userId)
            logger.info("$className.$executedFunc >> connected wholesaler fetched.")

            if (connectedWholesaler.isPresent) {
                cartItem.wholesaler = connectedWholesaler.get()
            }
            if (prevCartItem.isPresent) {
                cartItem.id = prevCartItem.get().id
            }
            val createdCartItem = cartItemRepository.save(cartItem)
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
                )

            )

            if (sameCartItem.isPresent) {
                item.quantity = sameCartItem.get().quantity!! + quantity
                cartItemRepository.deleteById(sameCartItem.get().id!!)
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
            cartItemRepository.deleteById(cartItemId)
            logger.info("$className.$executedFunc >> completed.")
            return cartItemId
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun upsertWholesalerOnCartItems(retailerId: Long, wholesalerId: Long): List<CartItem> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val retailer = userRepository.findById(retailerId)
            val wholesaler = userRepository.findById(wholesalerId)
            if (retailer.isEmpty || wholesaler.isEmpty) {
                val target = if (retailer.isEmpty) "retailer" else "wholesaler"
                val id = if (retailer.isEmpty) retailerId else wholesalerId
                throw CustomException(
                    message = "There's no $target whose ID is $id",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_USER
                )
            }
            logger.info("$className.$executedFunc >> retailer, wholesaler both are exists.")

            val cartItems = cartItemRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
            val updatedCartItemObjs = cartItems.map {
                it.wholesaler = entityManager.getReference(User::class.java, wholesalerId)
                it
            }
            val updatedCartItems = cartItemRepository.saveAll(updatedCartItemObjs)
            logger.info("$className.$executedFunc >> completed.")
            return updatedCartItems

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
    fun batchCartToOrder() {
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
                val wholesaler = cartItems[0].wholesaler

                val orderedAt = LocalDateTime.now()
                cartItemRepository.saveAll(
                    cartItems.map {
                        it.orderedAt = orderedAt
                        it
                    }
                )
                logger.info("$className.$executedFunc >> cart items are ordered. (${cartItems.map { it.id }})")

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

}