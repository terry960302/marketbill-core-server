package kr.co.marketbill.marketbillcoreserver.cart.domain.model

import kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity.ShoppingSessionJpo
import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.ShoppingSessionId
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User

data class ShoppingSession(
    val id: ShoppingSessionId? = null,
    val retailer: User? = null,
    val wholesaler: User? = null,
    val memo: Memo? = null,
    val cartItems: List<CartItem> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
) {
    fun updateWholesaler(newWholesaler: User): ShoppingSession {
        return copy(wholesaler = newWholesaler, updatedAt = LocalDateTime.now())
    }

    fun updateMemo(newMemo: Memo?): ShoppingSession {
        return copy(memo = newMemo, updatedAt = LocalDateTime.now())
    }

    fun addCartItem(cartItem: CartItem): ShoppingSession {
        val updatedCartItems = cartItems + cartItem
        return copy(cartItems = updatedCartItems, updatedAt = LocalDateTime.now())
    }

    fun removeCartItem(cartItemId: Long): ShoppingSession {
        val updatedCartItems = cartItems.filter { it.id?.value != cartItemId }
        return copy(cartItems = updatedCartItems, updatedAt = LocalDateTime.now())
    }

    fun updateCartItem(updatedCartItem: CartItem): ShoppingSession {
        val updatedCartItems =
            cartItems.map { if (it.id == updatedCartItem.id) updatedCartItem else it }
        return copy(cartItems = updatedCartItems, updatedAt = LocalDateTime.now())
    }

    fun delete(): ShoppingSession {
        return copy(deletedAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
    }

    fun isDeleted(): Boolean = deletedAt != null

    fun getActiveCartItems(): List<CartItem> {
        return cartItems.filter { !it.isDeleted() && !it.isOrdered() }
    }

    companion object {
        fun fromJpo(
            shoppingSessionJpo: ShoppingSessionJpo,
            retailer: User?,
            wholesaler: User?,
            cartItems: List<CartItem> = emptyList()
        ): ShoppingSession {
            return ShoppingSession(
                id = shoppingSessionJpo.id?.let { ShoppingSessionId(it) },
                retailer = retailer,
                wholesaler = wholesaler,
                memo = shoppingSessionJpo.memo?.let { Memo(it) },
                cartItems = cartItems,
                createdAt = shoppingSessionJpo.createdAt,
                updatedAt = shoppingSessionJpo.updatedAt,
                deletedAt = shoppingSessionJpo.deletedAt
            )
        }

        fun toJpo(shoppingSession: ShoppingSession): ShoppingSessionJpo {
            return ShoppingSessionJpo(
                id = shoppingSession.id?.value,
                retailerJpo = shoppingSession.retailer?.let { User.toJpo(it) },
                wholesalerJpo = shoppingSession.wholesaler?.let { User.toJpo(it) },
                memo = shoppingSession.memo?.value
            )
        }
    }
}
