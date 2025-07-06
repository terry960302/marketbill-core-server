package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.DailyOrderItemSearchCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.DailyOrderItemResult
import org.springframework.stereotype.Component

@Component
class DailyOrderItemSearchUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: DailyOrderItemSearchCommand): List<DailyOrderItemResult> {
        val dailyOrderItems = orderRepository.findDailyOrderItems(command.startedAt, command.endedAt, command.pageInfo)

        return dailyOrderItems.content.map { DailyOrderItemResult.from(it) }
    }
}
