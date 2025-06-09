package kr.co.marketbill.marketbillcoreserver.domain.validator

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.shared.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.exception.MarketbillException
import org.springframework.stereotype.Component

@Component
class ShoppingSessionValidator {
    fun validateCreateRequest(retailer: User, wholesaler: User?) {
        if (retailer.deletedAt != null) {
            throw MarketbillException(CustomErrorCode.NO_USER)
        }
        if (wholesaler?.deletedAt != null) {
            throw MarketbillException(CustomErrorCode.NO_USER)
        }
    }

    fun validateUpdateRequest(wholesaler: User?, memo: String?) {
        if (wholesaler == null && memo == null) {
            throw MarketbillException(CustomErrorCode.INVALID_DATA)
        }
        if (wholesaler?.deletedAt != null) {
            throw MarketbillException(CustomErrorCode.NO_USER)
        }
    }

    fun validateCartItemAddition(session: ShoppingSession, cartItem: CartItem) {
        if (cartItem.retailer.id != session.retailer?.id) {
            throw MarketbillException(CustomErrorCode.INVALID_DATA)
        }
        if (cartItem.wholesaler?.id != session.wholesaler?.id) {
            throw MarketbillException(CustomErrorCode.INVALID_DATA)
        }
    }

    fun validateShoppingSession(session: ShoppingSession) {
        if (session.retailer?.deletedAt != null) {
            throw MarketbillException(CustomErrorCode.NO_USER)
        }
        if (session.wholesaler?.deletedAt != null) {
            throw MarketbillException(CustomErrorCode.NO_USER)
        }
    }
}
