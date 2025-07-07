package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.mapper.toPageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import java.time.LocalDate

data class DailyOrderSheetAggregateByDateRangeCommand(
    val userId: UserId,
    val pageInfo: PageInfo
) {
    companion object {
        fun from(
            userId: Long,
            pagination: PaginationInput?
        ): DailyOrderSheetAggregateByDateRangeCommand {
            return DailyOrderSheetAggregateByDateRangeCommand(UserId.from(userId), pagination.toPageInfo())
        }
    }
}

