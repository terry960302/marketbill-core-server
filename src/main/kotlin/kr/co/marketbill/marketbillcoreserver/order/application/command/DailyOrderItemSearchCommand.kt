package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.mapper.toPageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.DailyOrderItemFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import java.time.LocalDate

data class DailyOrderItemSearchCommand(
    val startedAt: LocalDate?,
    val endedAt: LocalDate?,
    val pageInfo: PageInfo
) {

    companion object {
        fun from(filterInput: DailyOrderItemFilterInput?, pagination: PaginationInput?): DailyOrderItemSearchCommand {
            return DailyOrderItemSearchCommand(
                filterInput?.dateRange?.fromDate,
                filterInput?.dateRange?.toDate,
                pagination.toPageInfo()
            )
        }
    }
}
