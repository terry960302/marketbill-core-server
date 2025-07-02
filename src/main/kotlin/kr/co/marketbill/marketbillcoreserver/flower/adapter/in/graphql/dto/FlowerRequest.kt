package kr.co.marketbill.marketbillcoreserver.flower.adapter.`in`.graphql.dto

import kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.web.dto.DateRangeDto
import org.springframework.data.domain.Pageable

data class FlowerRequest(
    val dateRange : DateRangeDto,
    val keyword: String?,
    val pageable: Pageable
) {
}