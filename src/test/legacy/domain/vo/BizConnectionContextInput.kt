package kr.co.marketbill.marketbillcoreserver.legacy.domain.vo

import kr.co.marketbill.marketbillcoreserver.types.BizConnectionFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class BizConnectionContextInput(
    var pagination: PaginationInput? = null,
    var filter: BizConnectionFilterInput? = null,
)
