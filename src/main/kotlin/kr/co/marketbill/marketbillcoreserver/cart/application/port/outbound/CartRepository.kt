package kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound

import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.ShoppingSessionId
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId

interface CartRepository {
    fun saveCartItem(cartItem: CartItem): CartItem

    fun findCartItemById(cartItemId: CartItemId): CartItem?

    fun findCartItemsByRetailerId(retailerId: UserId): List<CartItem>

    fun deleteCartItem(cartItemId: CartItemId)

    fun saveShoppingSession(shoppingSession: ShoppingSession): ShoppingSession

    fun findShoppingSessionByRetailerId(retailerId: UserId): ShoppingSession?

    fun findShoppingSessionById(shoppingSessionId: ShoppingSessionId): ShoppingSession?

    fun findShoppingSessionsByRetailerIds(
            retailerIds: Set<UserId>
    ): Map<ShoppingSessionId, ShoppingSession>
}
