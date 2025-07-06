package kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.mapper

import kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity.ShoppingSessionJpo
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.ShoppingSessionId
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import org.springframework.stereotype.Component

@Component
class ShoppingSessionMapper {

//    fun toDomain(
//            shoppingSessionJpo: ShoppingSessionJpo,
//            retailer: User,
//            wholesaler: User?,
//            cartItems: List<CartItem> = emptyList()
//    ): ShoppingSession {
//        return ShoppingSession(
//                id = shoppingSessionJpo.id?.let { ShoppingSessionId(it) },
//                retailer = retailer,
//                wholesaler = wholesaler,
//                memo = shoppingSessionJpo.memo?.let { Memo(it) },
//                cartItems = cartItems,
//                createdAt = shoppingSessionJpo.createdAt,
//                updatedAt = shoppingSessionJpo.updatedAt,
//                deletedAt = shoppingSessionJpo.deletedAt
//        )
//    }
//
//    fun toJpo(shoppingSession: ShoppingSession): ShoppingSessionJpo {
//        return ShoppingSessionJpo(
//                id = shoppingSession.id?.value,
//                retailerId = shoppingSession.retailer.id.value,
//                wholesalerId = shoppingSession.wholesaler?.id?.value,
//                memo = shoppingSession.memo?.value
//        )
//    }
}
