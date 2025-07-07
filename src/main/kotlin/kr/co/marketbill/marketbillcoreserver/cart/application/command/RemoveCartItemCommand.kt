package kr.co.marketbill.marketbillcoreserver.cart.application.command

import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId

data class RemoveCartItemCommand(val cartItemId: CartItemId)
