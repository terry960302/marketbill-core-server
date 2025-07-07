package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.OrderSheetSearchCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetResult
import org.springframework.stereotype.Component

@Component
class OrderSheetSearchUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: OrderSheetSearchCommand): List<OrderSheetResult> {
        val orderSheets = orderRepository.findOrderSheetsByDateRange(command.getDate(), command.getDate(), command.toPageInfo())

        return orderSheets.content.map { OrderSheetResult.from(it) }
    }
}
