package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.UpdateOrderItemsPriceCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderItemResult
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderItemId
import org.springframework.stereotype.Component

@Component
class UpdateOrderItemsPriceUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: UpdateOrderItemsPriceCommand): List<OrderItemResult> {
        val updatedOrderItems =
            command.items.map { item ->
                val orderItemId = OrderItemId.from(item.orderItemId)
                val updatedOrderItem =
                    orderRepository.updateOrderItemPrice(orderItemId, item.price)
                OrderItemResult.from(updatedOrderItem)
            }

        return updatedOrderItems
    }
}
