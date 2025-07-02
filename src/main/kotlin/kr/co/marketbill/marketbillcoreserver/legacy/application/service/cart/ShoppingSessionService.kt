package kr.co.marketbill.marketbillcoreserver.legacy.application.service.cart

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.domain.specs.ShoppingSessionSpecs
import kr.co.marketbill.marketbillcoreserver.domain.validator.ShoppingSessionValidator
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.CartItemRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.ShoppingSessionRepository
import kr.co.marketbill.marketbillcoreserver.shared.constants.DEFAULT_PAGE
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class ShoppingSessionService(
        private val shoppingSessionRepository: ShoppingSessionRepository,
        private val cartItemRepository: CartItemRepository,
        private val shoppingSessionValidator: ShoppingSessionValidator
) {
    /** 소매상의 장바구니 세션을 조회하거나 생성합니다. */
    fun findOrCreateSession(retailerId: Long): ShoppingSession {
        return shoppingSessionRepository.findOne(ShoppingSessionSpecs.byRetailerId(retailerId))
                .orElseGet {
                    val newSession = ShoppingSession.createWith(retailerId)
                    shoppingSessionRepository.save(newSession)
                }
    }

    /** 장바구니 세션을 업데이트합니다. */
    fun updateSession(
            session: ShoppingSession,
            wholesalerId: Long?,
            memo: String?
    ): ShoppingSession {
        shoppingSessionValidator.validateUpdateRequest(wholesalerId, memo)
        shoppingSessionValidator.validateShoppingSession(session)

        val updatedSession =
                if (wholesalerId != null) {
                    updateSessionWithWholesaler(session, wholesalerId, memo)
                } else {
                    session.updateMemo(memo)
                }

        return shoppingSessionRepository.save(updatedSession)
    }

    /** 장바구니 세션에 아이템을 추가합니다. */
    fun addCartItemToSession(session: ShoppingSession, cartItem: CartItem): ShoppingSession {
        shoppingSessionValidator.validateShoppingSession(session)
        shoppingSessionValidator.validateCartItemAddition(session, cartItem)

        val updatedSession = session.addCartItem(cartItem)
        return shoppingSessionRepository.save(updatedSession)
    }

    /** 장바구니 세션에서 아이템을 제거합니다. */
    fun removeCartItemFromSession(session: ShoppingSession, cartItemId: Long): ShoppingSession {
        shoppingSessionValidator.validateShoppingSession(session)

        val updatedSession = session.removeCartItem(cartItemId)
        return shoppingSessionRepository.save(updatedSession)
    }

    /** 장바구니 세션을 삭제합니다. */
    fun deleteSession(session: ShoppingSession) {
        shoppingSessionRepository.delete(session)
    }

    /** 소매상 ID로 장바구니 세션을 조회합니다. */
    fun findSessionByRetailerId(retailerId: Long): ShoppingSession? {
        return shoppingSessionRepository
                .findOne(ShoppingSessionSpecs.byRetailerId(retailerId))
                .orElse(null)
    }

    /** 도매상 정보로 장바구니 세션을 업데이트합니다. */
    private fun updateSessionWithWholesaler(
            session: ShoppingSession,
            wholesalerId: Long,
            memo: String?
    ): ShoppingSession {
        val sessionWithWholesaler = session.updateWholesaler(wholesalerId)
        val savedSession = shoppingSessionRepository.save(sessionWithWholesaler)

        val cartItems =
                cartItemRepository.findAllByRetailerId(
                        session.retailerId,
                        PageRequest.of(DEFAULT_PAGE, 9999)
                )

        val updatedCartItems = cartItems.map { it.updateWholesaler(wholesalerId) }
        cartItemRepository.saveAll(updatedCartItems)

        return if (memo != null) {
            savedSession.updateMemo(memo)
        } else {
            savedSession
        }
    }
}
