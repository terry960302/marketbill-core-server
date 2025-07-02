package kr.co.marketbill.marketbillcoreserver.flower.application.command

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerSearchCriteria
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.FlowerFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FlowerSearchCommand(
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val keyword: String? = null,
    val page: Int = 0,
    val size: Int = 20
) {
    init {
        require(page >= 0) { "페이지는 0 이상이어야 합니다" }
        require(size > 0 && size <= 100) { "페이지 크기는 1-100 사이여야 합니다" }
    }

    companion object {
        fun fromGraphql(
            filter: FlowerFilterInput?,
            pagination: PaginationInput?
        ): FlowerSearchCommand {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fromDate = filter?.dateRange?.fromDate?.let { LocalDate.parse(it, dateFormatter) }
            val toDate = filter?.dateRange?.toDate?.let { LocalDate.parse(it, dateFormatter) }
            val keyword = filter?.keyword?.takeIf { !it.isNullOrBlank() }
            val page = pagination?.page ?: 0
            val size = pagination?.size ?: 15

            return FlowerSearchCommand(
                fromDate = fromDate,
                toDate = toDate,
                keyword = keyword,
                page = page,
                size = size
            )
        }
    }

    fun toCriteria(): FlowerSearchCriteria {
        return FlowerSearchCriteria.of(fromDate, toDate, keyword)
    }

    fun toPageInfo(): PageInfo {
        return PageInfo(page, size)
    }
}
