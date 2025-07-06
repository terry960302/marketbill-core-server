package kr.co.marketbill.marketbillcoreserver.order.application.command

import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.DateFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class OrderSheetSearchCommand(val filter: DateFilterInput?, val pagination: PaginationInput?) {
    fun toPageInfo(): PageInfo {
        return pagination?.let { PageInfo(page = it.page ?: 0, size = it.size ?: 10) }
            ?: PageInfo(page = 0, size = 10)
    }

    fun getDate(): LocalDate? {
        return filter?.date
    }
}

