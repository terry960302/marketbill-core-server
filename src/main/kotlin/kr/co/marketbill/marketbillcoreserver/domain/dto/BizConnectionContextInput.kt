package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.types.BizConnectionFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class BizConnectionContextInput(
    var pagination: PaginationInput? = null,
    var filter: BizConnectionFilterInput? = null,
)
