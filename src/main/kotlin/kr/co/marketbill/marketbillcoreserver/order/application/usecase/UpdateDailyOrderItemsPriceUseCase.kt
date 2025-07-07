package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.UpdateDailyOrderItemsPriceCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.DailyOrderItemResult
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.DailyOrderItemId
import org.springframework.stereotype.Component

@Component
class UpdateDailyOrderItemsPriceUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: UpdateDailyOrderItemsPriceCommand): List<DailyOrderItemResult> {
        val updatedDailyOrderItems =
            command.items.map { item ->
                val dailyOrderItemId = DailyOrderItemId.from(item.dailyOrderItemId)
                val updatedDailyOrderItem =
                    orderRepository.updateDailyOrderItemPrice(dailyOrderItemId, item.price)
                DailyOrderItemResult.from(updatedDailyOrderItem)
            }

        return updatedDailyOrderItems
    }
}
