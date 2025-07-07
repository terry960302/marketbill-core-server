package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.mapper.toPageInfo
import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.DateFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class OrderItemSearchCommand(val date: LocalDate?, val pageInfo: PageInfo) {
    companion object {
        fun from(filter: DateFilterInput?, pagination: PaginationInput?): OrderItemSearchCommand {
            return OrderItemSearchCommand(
                date = filter?.date,
                pageInfo = pagination.toPageInfo()
            )
        }
    }
}
