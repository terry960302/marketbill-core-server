package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.BatchCartToOrderLogs
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.BatchCartToOrderLogsRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.CartItemSpecs
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter.Companion.convertFlowerGradeToKor
import kr.co.marketbill.marketbillcoreserver.util.StringGenerator
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
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var orderSheetRepository: OrderSheetRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var batchCartToOrderLogsRepository: BatchCartToOrderLogsRepository

    val logger: Logger = LoggerFactory.getLogger(CartService::class.java)


    fun getAllCartItems(userId: Long, pageable: Pageable): Page<CartItem> {
        return cartRepository.findAllByRetailerId(userId, pageable)
    }

    fun addToCart(userId: Long, flowerId: Long, quantity: Int, grade: FlowerGrade): CartItem {
        val cartItem = CartItem(
            retailer = entityManager.getReference(User::class.java, userId),
            flower = entityManager.getReference(Flower::class.java, flowerId),
            quantity = quantity,
            grade = convertFlowerGradeToKor(grade)
        )
        return cartRepository.save(cartItem)
    }

    fun updateCartItem(id: Long, quantity: Int, grade: FlowerGrade): CartItem {
        val cartItem = cartRepository.findById(id)
        if (cartItem.isEmpty) throw CustomException("There's no cart_item whose ID is $id")

        val item = cartItem.get()
        item.quantity = quantity
        item.grade = convertFlowerGradeToKor(grade)

        return cartRepository.save(item)
    }

    fun removeCartItem(cartItemId: Long): Long {
        try {
            cartRepository.deleteById(cartItemId)
            return cartItemId
        } catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
    }

    fun upsertWholesalerOnCartItems(retailerId: Long, wholesalerId: Long): List<CartItem> {
        val cartItems = cartRepository.findAllByRetailerId(retailerId, PageRequest.of(DEFAULT_PAGE, 9999))
        val updatedCartItems = cartItems.map {
            it.wholesaler = entityManager.getReference(User::class.java, wholesalerId)
            it
        }
        return cartRepository.saveAll(updatedCartItems)
    }

    /**
     * <스케쥴러>
     *
     * : 매일 오후 11시에 자동 주문처리
     */
    @Scheduled(cron = "0 0 23 * * ?", zone = "Asia/Seoul")
    @Transactional
    fun batchCartToOrder() {
        val newOrderSheets = mutableListOf<OrderSheet>()
        val newOrderItems = mutableListOf<OrderItem>()
        val validCartItems = cartRepository.findAll(CartItemSpecs.hasWholesaler()).filter { it.wholesaler != null }

        val log = BatchCartToOrderLogs(
            cartItemsCount = validCartItems.size,
            orderSheetCount = -1,
            orderItemCount = -1,
            errLogs = ""
        )

        try {
            val cartItemGroup: Map<Long, List<CartItem>> = validCartItems.groupBy { it.retailer!!.id!! }

            cartItemGroup.forEach { (retailerId, cartItems) ->
                val retailer = entityManager.getReference(User::class.java, retailerId)
                val wholesaler = cartItems[0].wholesaler

                val orderedAt = LocalDateTime.now()
                cartRepository.saveAll(
                    cartItems.map {
                        it.orderedAt = orderedAt
                        it
                    }
                )

                val orderSheet = OrderSheet(
                    orderNo = "",
                    retailer = retailer,
                    wholesaler = wholesaler,
                )
                newOrderSheets.add(orderSheet)
                val savedOrderSheet = orderSheetRepository.save(orderSheet)
                savedOrderSheet.orderNo = StringGenerator.generateOrderNo(savedOrderSheet.id!!)
                val updatedOrderSheet = orderSheetRepository.save(savedOrderSheet)

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
            }



            log.orderSheetCount = newOrderSheets.size
            log.orderItemCount = newOrderItems.size
        } catch (e: java.lang.Exception) {
            log.errLogs = e.message
            throw e
        } finally {
            batchCartToOrderLogsRepository.save(log)
        }
    }

}