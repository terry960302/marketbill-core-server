package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.DailyOrderSheetAggregateByDateCommand
import kr.co.marketbill.marketbillcoreserver.order.application.command.DailyOrderSheetAggregateByDateRangeCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetsAggregateResult
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId

@Component
class DailyOrderSheetAggregateUseCase(private val orderRepository: OrderRepository) {
    private val periodOfMonth: Long = 3L

    fun executeUntilPast3Months(command: DailyOrderSheetAggregateByDateRangeCommand): PageResult<OrderSheetsAggregateResult> {
        val curDate = LocalDate.now()
        val dateBeforeThreeMonth = LocalDate.from(
            LocalDate.now().minusMonths(periodOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        return orderRepository.findOrderSheetsAggregateByDateRange(
            command.userId,
            dateBeforeThreeMonth,
            curDate,
            command.pageInfo
        )
    }

    fun executeByDate(command: DailyOrderSheetAggregateByDateCommand): OrderSheetsAggregateResult? {
        return orderRepository.findOrderSheetsAggregateByDate(command.date)
    }
}
