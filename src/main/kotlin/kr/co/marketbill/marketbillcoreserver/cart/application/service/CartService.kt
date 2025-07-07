package kr.co.marketbill.marketbillcoreserver.cart.application.service

import kr.co.marketbill.marketbillcoreserver.cart.application.command.*
import kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound.CartRepository
import kr.co.marketbill.marketbillcoreserver.cart.application.usecase.*
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.ShoppingSessionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(
        private val addToCartUseCase: AddToCartUseCase,
        private val updateCartItemUseCase: UpdateCartItemUseCase,
        private val removeCartItemUseCase: RemoveCartItemUseCase,
        private val findShoppingSessionUseCase: FindShoppingSessionUseCase,
        private val updateShoppingSessionUseCase: UpdateShoppingSessionUseCase,
        private val cartRepository: CartRepository
) {
    fun addToCart(command: AddToCartCommand): CartItem {
        return addToCartUseCase.execute(command)
    }

    fun updateCartItem(command: UpdateCartItemCommand): CartItem {
        return updateCartItemUseCase.execute(command)
    }

    fun removeCartItem(command: RemoveCartItemCommand) {
        removeCartItemUseCase.execute(command)
    }

    fun findShoppingSession(command: FindShoppingSessionCommand): ShoppingSession? {
        return findShoppingSessionUseCase.execute(command)
    }

    fun findShoppingSessionsByRetailerIds(
            command: FindShoppingSessionCommand
    ): Map<ShoppingSessionId, ShoppingSession> {
        return findShoppingSessionUseCase.executeBatch(command)
    }

    fun updateShoppingSession(command: UpdateShoppingSessionCommand): ShoppingSession {
        return updateShoppingSessionUseCase.execute(command)
    }
}
