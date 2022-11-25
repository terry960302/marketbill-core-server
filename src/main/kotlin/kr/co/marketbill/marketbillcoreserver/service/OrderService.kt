package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderItemSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.OrderSheetSpecs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.Optional
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

    val logger: Logger = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun orderCartItems(cartItemIds: List<Long>, wholesalerId: Long): OrderSheet {
        val cartItems: List<CartItem> = cartItemIds.map {
            entityManager.getReference(CartItem::class.java, it)
        }.map {
            it.orderedAt = LocalDateTime.now()
            it
        }
        cartRepository.saveAll(cartItems)

        val selectedWholesaler = entityManager.getReference(User::class.java, wholesalerId)
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
}