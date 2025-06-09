package kr.co.marketbill.marketbillcoreserver.application.service.cart

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.domain.specs.CartItemSpecs
import kr.co.marketbill.marketbillcoreserver.domain.validator.CartItemValidator
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.CartItemRepository
import kr.co.marketbill.marketbillcoreserver.shared.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.exception.MarketbillException
import org.springframework.stereotype.Component

@Component
class CartItemService(
        private val cartItemRepository: CartItemRepository,
        private val cartItemValidator: CartItemValidator
) {
    /** 장바구니 아이템을 생성합니다. */
    fun createCartItem(
            session: ShoppingSession,
            flowerId: Long,
            grade: String,
            quantity: Int
    ): CartItem {
        cartItemValidator.validateCreateRequest(flowerId, quantity)

        val newCartItem =
                CartItem.createWith(
                        shoppingSession = session,
                        flowerId = flowerId,
                        grade = grade,
                        quantity = quantity
                )

        cartItemValidator.validateCartItem(newCartItem)
        return cartItemRepository.save(newCartItem)
    }

    /** 장바구니 아이템을 수정합니다. */
    fun updateCartItem(cartItem: CartItem, quantity: Int): CartItem {
        cartItemValidator.validateUpdateRequest(quantity)

        val updatedCartItem = cartItem.updateQuantity(quantity)
        cartItemValidator.validateCartItem(updatedCartItem)

        return cartItemRepository.save(updatedCartItem)
    }

    /** 장바구니 아이템을 주문 상태로 변경합니다. */
    fun markAsOrdered(cartItem: CartItem): CartItem {
        val orderedCartItem = cartItem.markAsOrdered()
        return cartItemRepository.save(orderedCartItem)
    }

    /** ID로 장바구니 아이템을 조회합니다. */
    fun findCartItemById(id: Long): CartItem {
        return cartItemRepository.findById(id).orElseThrow {
            MarketbillException(CustomErrorCode.NO_CART_ITEM)
        }
    }

    /** 같은 상품의 장바구니 아이템을 찾습니다. */
    private fun findSameCartItem(cartItem: CartItem, grade: String): CartItem? {
        return cartItemRepository
                .findOne(
                        CartItemSpecs.excludeId(cartItem.id)
                                .and(CartItemSpecs.byRetailerId(cartItem.retailerId))
                                .and(CartItemSpecs.byFlowerId(cartItem.flowerId))
                                .and(CartItemSpecs.byFlowerGrade(grade))
                                .and(CartItemSpecs.byShoppingSessionId(cartItem.shoppingSession.id))
                )
                .orElse(null)
    }
}
