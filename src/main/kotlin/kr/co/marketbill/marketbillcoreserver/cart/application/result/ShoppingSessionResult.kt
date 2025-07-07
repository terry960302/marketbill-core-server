package kr.co.marketbill.marketbillcoreserver.cart.application.result

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.ShoppingSessionId
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult

data class ShoppingSessionResult(
        val id: ShoppingSessionId,
        val retailer: UserResult,
        val wholesaler: UserResult?,
        val memo: Memo?,
        val cartItems: List<CartItemResult>,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
)
