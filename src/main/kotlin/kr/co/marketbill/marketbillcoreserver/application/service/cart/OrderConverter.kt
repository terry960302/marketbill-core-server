package kr.co.marketbill.marketbillcoreserver.application.service.cart

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.shared.util.StringGenerator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderConverter(
        private val orderSheetRepository: OrderSheetRepository,
        private val orderItemRepository: OrderItemRepository
) {
    /** 장바구니 아이템들을 주문으로 변환합니다. */
    @Transactional
    fun convertCartItemsToOrder(
            retailer: User,
            wholesaler: User,
            cartItems: List<CartItem>
    ): Pair<OrderSheet, List<OrderItem>> {
        val orderSheet = createOrderSheet(retailer, wholesaler)
        val orderItems = createOrderItems(orderSheet, retailer, wholesaler, cartItems)
        return Pair(orderSheet, orderItems)
    }

    /** 주문서를 생성합니다. */
    private fun createOrderSheet(retailer: User, wholesaler: User): OrderSheet {
        val orderSheet = OrderSheet(orderNo = "", retailer = retailer, wholesaler = wholesaler)
        val savedOrderSheet = orderSheetRepository.save(orderSheet)
        savedOrderSheet.orderNo = StringGenerator.generateOrderNo(savedOrderSheet.id!!)
        return orderSheetRepository.save(savedOrderSheet)
    }

    /** 주문 아이템들을 생성합니다. */
    private fun createOrderItems(
            orderSheet: OrderSheet,
            retailer: User,
            wholesaler: User,
            cartItems: List<CartItem>
    ): List<OrderItem> {
        val orderItems =
                cartItems.map {
                    OrderItem(
                            orderSheet = orderSheet,
                            retailer = retailer,
                            wholesaler = wholesaler,
                            flower = it.flower,
                            quantity = it.quantity,
                            grade = it.grade,
                            price = null
                    )
                }
        return orderItemRepository.saveAll(orderItems)
    }
}
