package kr.co.marketbill.marketbillcoreserver.domain.vo

import java.time.LocalDate

data class DailyOrderItemKey(
    val wholesalerId: Long,
    val flowerId: Long,
    val grade: String,
    val date : LocalDate,
)
