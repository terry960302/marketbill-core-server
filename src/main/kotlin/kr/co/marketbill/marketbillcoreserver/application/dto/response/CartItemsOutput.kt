package kr.co.marketbill.marketbillcoreserver.application.dto.response

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import org.springframework.data.domain.Page

data class CartItemsOutput(
    val resultCount : Long,
    val items : Page<CartItem>
)
