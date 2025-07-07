package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.OrderItemSearchCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderItemResult
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class OrderItemSearchUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: OrderItemSearchCommand): List<OrderItemResult> {
        val orderItems = orderRepository.findOrderItemsByDateRange(
            command.date,
            command.date,
            command.pageInfo,
        )

        return orderItems.content.map { OrderItemResult.from(it) }
    }
}
