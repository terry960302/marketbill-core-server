package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.web.dto

import java.time.LocalDate

data class DateRangeDto(
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)