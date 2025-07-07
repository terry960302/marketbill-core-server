package kr.co.marketbill.marketbillcoreserver.cart.application.result

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult

data class CartItemResult(
        val id: CartItemId,
        val retailer: UserResult,
        val wholesaler: UserResult?,
        val flowerId: Long,
        val flowerName: String,
        val flowerTypeName: String,
        val quantity: Quantity,
        val grade: FlowerGrade,
        val memo: Memo?,
        val orderedAt: LocalDateTime?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
)
