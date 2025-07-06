package kr.co.marketbill.marketbillcoreserver.cart.application.command

import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade

data class UpdateCartItemCommand(
        val id: CartItemId,
        val quantity: Quantity,
        val grade: FlowerGrade,
        val memo: Memo?
)
