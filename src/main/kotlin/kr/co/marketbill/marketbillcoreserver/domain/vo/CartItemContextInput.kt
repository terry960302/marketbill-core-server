package kr.co.marketbill.marketbillcoreserver.domain.vo

import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class CartItemContextInput(
    var pagination: PaginationInput? = null
)
