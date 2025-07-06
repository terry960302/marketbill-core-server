package kr.co.marketbill.marketbillcoreserver.cart.application.usecase

import kr.co.marketbill.marketbillcoreserver.cart.application.command.RemoveCartItemCommand
import kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound.CartRepository
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.stereotype.Component

@Component
class RemoveCartItemUseCase(private val cartRepository: CartRepository) {
    fun execute(command: RemoveCartItemCommand) {
        val cartItem =
                cartRepository.findCartItemById(command.cartItemId)
                        ?: throw MarketbillException(ErrorCode.NO_CART_ITEM)

        if (cartItem.isDeleted()) {
            throw MarketbillException(ErrorCode.CART_ITEM_ALREADY_DELETED)
        }

        if (cartItem.isOrdered()) {
            throw MarketbillException(ErrorCode.CART_ITEM_ALREADY_ORDERED)
        }

        cartRepository.deleteCartItem(command.cartItemId)
    }
}
