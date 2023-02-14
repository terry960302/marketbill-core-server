package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class CustomOrderItemContextInput(
    var pagination: PaginationInput? = null
)
