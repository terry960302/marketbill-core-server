package kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.mapper

import kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity.CartItemJpo
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import org.springframework.stereotype.Component

@Component
class CartItemMapper {

//    fun toDomain(
//        cartItemJpo: CartItemJpo,
//        retailer: User,
//        wholesaler: User?,
//        flower: Flower
//    ): CartItem {
//        return CartItem(
//            id = cartItemJpo.id?.let { CartItemId(it) },
//            retailer = retailer,
//            wholesaler = wholesaler,
//            flower = flower,
//            quantity = Quantity(cartItemJpo.quantity),
//            grade = cartItemJpo.grade,
//            memo = cartItemJpo.memo?.let { Memo(it) },
//            orderedAt = cartItemJpo.orderedAt,
//            createdAt = cartItemJpo.createdAt,
//            updatedAt = cartItemJpo.updatedAt,
//            deletedAt = cartItemJpo.deletedAt
//        )
//    }
//
//    fun toJpo(cartItem: CartItem): CartItemJpo {
//        return CartItemJpo(
//            id = cartItem.id?.value,
//            retailerId = cartItem.retailer.id?.let { it.value },
//            wholesalerId = cartItem.wholesaler?.id?.value,
//            flowerId = cartItem.flower.id.value,
//            quantity = cartItem.quantity.value,
//            grade = cartItem.grade,
//            memo = cartItem.memo?.value,
//            orderedAt = cartItem.orderedAt
//        )
//    }
}
