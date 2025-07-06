package kr.co.marketbill.marketbillcoreserver.cart.application.command

import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId

data class UpdateShoppingSessionCommand(
        val retailerId: UserId,
        val wholesalerId: UserId?,
        val memo: Memo?
)
