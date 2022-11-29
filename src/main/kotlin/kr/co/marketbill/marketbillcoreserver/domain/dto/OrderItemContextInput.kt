package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class OrderItemContextInput(
    var pagination: PaginationInput? = null
)
