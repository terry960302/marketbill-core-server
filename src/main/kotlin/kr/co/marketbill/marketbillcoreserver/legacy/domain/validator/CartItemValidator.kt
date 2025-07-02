package kr.co.marketbill.marketbillcoreserver.legacy.domain.validator

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.exception.MarketbillException
import org.springframework.stereotype.Component

@Component
class CartItemValidator {
    fun validateCreateRequest(retailer: User, quantity: Int) {
        if (quantity <= 0) {
            throw MarketbillException(ErrorCode.INVALID_DATA)
        }
        if (retailer.deletedAt != null) {
            throw MarketbillException(ErrorCode.NO_USER)
        }
    }

    fun validateUpdateRequest(quantity: Int) {
        if (quantity <= 0) {
            throw MarketbillException(ErrorCode.INVALID_DATA)
        }
    }

    fun validateCartItem(cartItem: CartItem) {
        if (cartItem.quantity <= 0) {
            throw MarketbillException(ErrorCode.INVALID_DATA)
        }
        if (cartItem.retailer.deletedAt != null) {
            throw MarketbillException(ErrorCode.NO_USER)
        }
        if (cartItem.wholesaler?.deletedAt != null) {
            throw MarketbillException(ErrorCode.NO_USER)
        }
    }
}
