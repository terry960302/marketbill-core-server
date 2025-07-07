package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.dto

import java.time.LocalDate

data class DateRangeDto(
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)