package kr.co.marketbill.marketbillcoreserver.application.service.cart

import java.util.Optional
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.CartItemRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.ShoppingSessionRepository
import kr.co.marketbill.marketbillcoreserver.shared.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.shared.exception.MarketbillException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CartService(
        private val cartItemService: CartItemService,
        private val shoppingSessionService: ShoppingSessionService,
        private val cartItemRepository: CartItemRepository,
        private val shoppingSessionRepository: ShoppingSessionRepository
) {
    /** 소매상의 장바구니 세션을 조회합니다. */
    fun getShoppingSession(retailerId: Long): Optional<ShoppingSession> {
        return Optional.ofNullable(shoppingSessionService.findSessionByRetailerId(retailerId))
    }

    /** 장바구니에 상품을 추가합니다. */
    @Transactional
    fun addCartItem(userId: Long, flowerId: Long, grade: FlowerGrade, quantity: Int): CartItem {
        val session = shoppingSessionService.findOrCreateSession(userId)
        return cartItemService.createCartItem(session, flowerId, grade.name, quantity)
    }

    /** 장바구니 상품을 수정합니다. */
    @Transactional
    fun updateCartItem(userId: Long, cartItemId: Long, quantity: Int): CartItem {
        val cartItem = cartItemService.findCartItemById(cartItemId)
        return cartItemService.updateCartItem(cartItem, quantity)
    }

    /** 장바구니 상품을 삭제합니다. */
    @Transactional
    fun removeCartItem(cartItemId: Long): Long {
        val cartItem = cartItemService.findCartItemById(cartItemId)
        val session = cartItem.shoppingSession
        shoppingSessionService.removeCartItemFromSession(session, cartItemId)
        return cartItemId
    }

    /** 장바구니 세션을 업데이트합니다. */
    @Transactional
    fun updateShoppingSession(userId: Long, wholesalerId: Long?, memo: String?): ShoppingSession {
        val session =
                shoppingSessionService.findSessionByRetailerId(userId)
                        ?: throw MarketbillException(CustomErrorCode.NO_SHOPPING_SESSION)
        return shoppingSessionService.updateSession(session, wholesalerId, memo)
    }

    /** 여러 장바구니 세션의 상품을 페이지네이션하여 조회합니다. */
    fun getAllPaginatedCartItemsByShoppingSessionIds(
            shoppingSessionIds: List<Long>,
            pageable: Pageable
    ): Map<Long, Page<CartItem>> {
        val startRow = pageable.pageSize * pageable.pageNumber
        val endRow = startRow + pageable.pageSize

        val cartItems =
                cartItemRepository.getAllPaginatedCartItemsBySessionIds(
                        shoppingSessionIds,
                        startRow,
                        endRow
                )

        return cartItems.groupBy { it.shoppingSession.id!! }.mapValues { (_, items) ->
            PageImpl(items, pageable, items.size.toLong())
        }
    }

    /** 장바구니 상품들을 일괄 주문합니다. */
    @Transactional
    fun orderBatchCartItems(retailerId: Long): List<CartItem> {
        val session =
                shoppingSessionService.findSessionByRetailerId(retailerId)
                        ?: throw MarketbillException(CustomErrorCode.NO_SHOPPING_SESSION)

        val cartItems =
                cartItemRepository.findAllByRetailerId(retailerId, Pageable.unpaged()).content
        if (cartItems.isEmpty()) {
            throw MarketbillException(CustomErrorCode.NO_CART_ITEM)
        }

        val orderedItems = cartItems.map { cartItem -> cartItemService.markAsOrdered(cartItem) }
        shoppingSessionService.deleteSession(session)
        return orderedItems
    }
}
