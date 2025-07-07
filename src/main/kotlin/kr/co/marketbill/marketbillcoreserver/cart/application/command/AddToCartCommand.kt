package kr.co.marketbill.marketbillcoreserver.cart.application.command

import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId

data class AddToCartCommand(
        val flowerId: FlowerId,
        val quantity: Quantity,
        val grade: FlowerGrade,
        val memo: Memo?,
        val retailerId: UserId
)
