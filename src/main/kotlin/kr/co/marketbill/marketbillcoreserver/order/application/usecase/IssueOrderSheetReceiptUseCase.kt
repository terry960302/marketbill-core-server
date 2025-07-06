package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.application.command.IssueOrderSheetReceiptCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetReceiptResult
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.stereotype.Component

@Component
class IssueOrderSheetReceiptUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: IssueOrderSheetReceiptCommand): OrderSheetReceiptResult {
        val orderSheetId = OrderSheetId.from(command.orderSheetId)

        // 주문서가 존재하는지 확인
        val orderSheet =
            orderRepository.findOrderSheetById(orderSheetId)
                ?: throw MarketbillException(ErrorCode.NO_ORDER_SHEET)

        val now = LocalDateTime.now()
        val receipt =
            OrderSheetReceipt(
                id = null,
                orderSheet = orderSheet,
                fileName = generateReceiptFileName(orderSheet.orderNo),
                filePath = generateReceiptFilePath(orderSheet.orderNo),
                fileFormat = "pdf",
                metadata = "{}",
                createdAt = now,
                updatedAt = now,
                deletedAt = null
            )

        val savedReceipt = orderRepository.saveOrderSheetReceipt(receipt)
        return OrderSheetReceiptResult.from(savedReceipt)
    }

    private fun generateReceiptFileName(orderNo: String): String {
        return "receipt_${orderNo}_${System.currentTimeMillis()}"
    }

    private fun generateReceiptFilePath(orderNo: String): String {
        return "/receipts/${orderNo}/${generateReceiptFileName(orderNo)}.pdf"
    }
}
