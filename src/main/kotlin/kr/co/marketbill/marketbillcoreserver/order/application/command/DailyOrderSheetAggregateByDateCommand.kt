package kr.co.marketbill.marketbillcoreserver.order.application.command

import java.time.LocalDate

data class DailyOrderSheetAggregateByDateCommand(val date: LocalDate?) {
    companion object {
        fun from(date: LocalDate?): DailyOrderSheetAggregateByDateCommand {
            return DailyOrderSheetAggregateByDateCommand(date)
        }
    }
}
