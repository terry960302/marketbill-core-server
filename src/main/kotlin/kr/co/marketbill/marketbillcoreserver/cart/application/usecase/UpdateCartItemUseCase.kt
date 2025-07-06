package kr.co.marketbill.marketbillcoreserver.cart.application.usecase

import kr.co.marketbill.marketbillcoreserver.cart.application.command.UpdateCartItemCommand
import kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound.CartRepository
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.stereotype.Component

@Component
class UpdateCartItemUseCase(private val cartRepository: CartRepository) {
    fun execute(command: UpdateCartItemCommand): CartItem {
        val existingCartItem =
                cartRepository.findCartItemById(command.id)
                        ?: throw MarketbillException(ErrorCode.NO_CART_ITEM)

        if (existingCartItem.isDeleted()) {
            throw MarketbillException(ErrorCode.CART_ITEM_ALREADY_DELETED)
        }

        if (existingCartItem.isOrdered()) {
            throw MarketbillException(ErrorCode.CART_ITEM_ALREADY_ORDERED_UPDATE)
        }

        val updatedCartItem =
                existingCartItem
                        .updateQuantity(command.quantity)
                        .updateGrade(command.grade)
                        .updateMemo(command.memo)

        return cartRepository.saveCartItem(updatedCartItem)
    }
}
