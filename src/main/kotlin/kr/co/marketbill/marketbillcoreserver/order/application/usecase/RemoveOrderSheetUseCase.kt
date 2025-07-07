package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import kr.co.marketbill.marketbillcoreserver.order.application.command.RemoveOrderSheetCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.stereotype.Component

@Component
class RemoveOrderSheetUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: RemoveOrderSheetCommand): Boolean {
        val orderSheetId = OrderSheetId.from(command.orderSheetId)

        // 주문서가 존재하는지 확인
        val orderSheet =
            orderRepository.findOrderSheetById(orderSheetId)
                ?: throw MarketbillException(ErrorCode.NO_ORDER_SHEET)

        // 영수증이 발행된 주문서는 삭제할 수 없음
        if (orderSheet.hasReceipt) {
            throw MarketbillException(ErrorCode.CANNOT_DELETE_ORDER_SHEET_WITH_RECEIPT)
        }

        return orderRepository.deleteOrderSheet(orderSheetId)
    }
}
