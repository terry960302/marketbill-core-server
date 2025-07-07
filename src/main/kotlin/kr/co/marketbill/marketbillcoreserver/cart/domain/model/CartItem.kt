package kr.co.marketbill.marketbillcoreserver.cart.domain.model

import kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity.CartItemJpo
import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User

data class CartItem(
        val id: CartItemId? = null,
        val retailer: User? = null,
        val wholesaler: User? = null,
        val flower: Flower? = null,
        val quantity: Quantity,
        val grade: FlowerGrade,
        val memo: Memo? = null,
        val orderedAt: LocalDateTime? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val deletedAt: LocalDateTime? = null
) {
    fun updateQuantity(newQuantity: Quantity): CartItem {
        return copy(quantity = newQuantity, updatedAt = LocalDateTime.now())
    }

    fun updateGrade(newGrade: FlowerGrade): CartItem {
        return copy(grade = newGrade, updatedAt = LocalDateTime.now())
    }

    fun updateMemo(newMemo: Memo?): CartItem {
        return copy(memo = newMemo, updatedAt = LocalDateTime.now())
    }

    fun updateWholesaler(newWholesaler: User): CartItem {
        return copy(wholesaler = newWholesaler, updatedAt = LocalDateTime.now())
    }

    fun markAsOrdered(): CartItem {
        return copy(orderedAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
    }

    fun delete(): CartItem {
        return copy(deletedAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
    }

    fun isDeleted(): Boolean = deletedAt != null

    fun isOrdered(): Boolean = orderedAt != null

    companion object{
        fun fromJpo(
            cartItemJpo: CartItemJpo,
            retailer: User?,
            wholesaler: User?,
            flower: Flower?
        ): CartItem {
            return CartItem(
                id = cartItemJpo.id?.let { CartItemId(it) },
                retailer = retailer,
                wholesaler = wholesaler,
                flower = flower,
                quantity = Quantity(cartItemJpo.quantity),
                grade = cartItemJpo.grade,
                memo = cartItemJpo.memo?.let { Memo(it) },
                orderedAt = cartItemJpo.orderedAt,
                createdAt = cartItemJpo.createdAt,
                updatedAt = cartItemJpo.updatedAt,
                deletedAt = cartItemJpo.deletedAt
            )
        }

        fun toJpo(cartItem: CartItem): CartItemJpo {
            return CartItemJpo(
                id = cartItem.id?.value,
                retailerJpo = cartItem.retailer?.let { User.toJpo(cartItem.retailer) },
                wholesalerJpo = cartItem.wholesaler?.let { User.toJpo(cartItem.wholesaler) },
                flowerJpo = cartItem.flower?.let { Flower.toJpo(cartItem.flower) },
                quantity = cartItem.quantity.value,
                grade = cartItem.grade,
                memo = cartItem.memo?.value,
                orderedAt = cartItem.orderedAt,
            )
        }
    }
}
