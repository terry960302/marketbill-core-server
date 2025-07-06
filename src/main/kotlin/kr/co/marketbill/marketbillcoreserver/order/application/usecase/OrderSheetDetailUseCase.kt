package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.OrderSheetDetailCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetResult
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.stereotype.Component

@Component
class OrderSheetDetailUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: OrderSheetDetailCommand): OrderSheetResult {
        val orderSheetId = command.toOrderSheetId()

        val orderSheet =
                orderRepository.findOrderSheetById(orderSheetId)
                        ?: throw MarketbillException(ErrorCode.NO_ORDER_SHEET)

        return OrderSheetResult.from(orderSheet)
    }
}
