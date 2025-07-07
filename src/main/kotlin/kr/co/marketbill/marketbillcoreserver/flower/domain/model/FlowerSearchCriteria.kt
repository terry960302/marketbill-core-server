package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import java.time.LocalDate

data class FlowerSearchCriteria(
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val keyword: String? = null,
) {
    init {
        if (fromDate != null && toDate != null) {
            require(!fromDate.isAfter(toDate)) { "시작일은 종료일보다 빠를 수 없습니다" }
        }
    }

    companion object {
        fun of(
            fromDate: LocalDate?,
            toDate: LocalDate?,
            keyword: String?,
        ): FlowerSearchCriteria {
            return FlowerSearchCriteria(
                fromDate = fromDate,
                toDate = toDate,
                keyword = keyword?.takeIf { it.isNotBlank() },
            )
        }
    }
}
