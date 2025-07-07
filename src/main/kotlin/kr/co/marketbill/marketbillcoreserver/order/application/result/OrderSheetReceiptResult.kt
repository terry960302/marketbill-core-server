package kr.co.marketbill.marketbillcoreserver.order.application.result

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheetReceipt

data class OrderSheetReceiptResult(
        val id: Long,
        val orderSheet: OrderSheetResult,
        val fileName: String,
        val filePath: String,
        val fileFormat: String,
        val metadata: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    companion object {
        fun from(orderSheetReceipt: OrderSheetReceipt): OrderSheetReceiptResult {
            return OrderSheetReceiptResult(
                    id = orderSheetReceipt.id?.value ?: 0L,
                    orderSheet = OrderSheetResult.from(orderSheetReceipt.orderSheet),
                    fileName = orderSheetReceipt.fileName,
                    filePath = orderSheetReceipt.filePath,
                    fileFormat = orderSheetReceipt.fileFormat,
                    metadata = orderSheetReceipt.metadata,
                    createdAt = orderSheetReceipt.createdAt,
                    updatedAt = orderSheetReceipt.updatedAt,
                    deletedAt = orderSheetReceipt.deletedAt
            )
        }
    }
}
