package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.RemoveCustomOrderItemCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.CustomOrderItemId
import org.springframework.stereotype.Component

@Component
class RemoveCustomOrderItemUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: RemoveCustomOrderItemCommand): Boolean {
        return orderRepository.deleteCustomOrderItems(command.customOrderItemIds.map { CustomOrderItemId.from(it) })
    }
}
