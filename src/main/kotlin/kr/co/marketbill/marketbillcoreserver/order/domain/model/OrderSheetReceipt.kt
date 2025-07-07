package kr.co.marketbill.marketbillcoreserver.order.domain.model

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.OrderSheetReceiptJpo
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetReceiptId

data class OrderSheetReceipt(
        val id: OrderSheetReceiptId?,
        val orderSheet: OrderSheet,
        val fileName: String,
        val filePath: String,
        val fileFormat: String,
        val metadata: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    init {
        require(fileName.isNotBlank()) { "파일명은 필수입니다" }
        require(filePath.isNotBlank()) { "파일경로는 필수입니다" }
        require(fileFormat.isNotBlank()) { "파일확장자는 필수입니다" }
    }

    fun getFullFileName(): String = "$fileName.$fileFormat"

    companion object {
        fun fromJpo(jpo: OrderSheetReceiptJpo, orderSheet: OrderSheet? = null): OrderSheetReceipt {
            return OrderSheetReceipt(
                    id = jpo.id?.let { OrderSheetReceiptId.from(it) },
                    orderSheet = orderSheet ?: OrderSheet.fromJpo(jpo.orderSheet),
                    fileName = jpo.fileName,
                    filePath = jpo.filePath,
                    fileFormat = jpo.fileFormat,
                    metadata = jpo.metadata,
                    createdAt = jpo.createdAt,
                    updatedAt = jpo.updatedAt,
                    deletedAt = jpo.deletedAt
            )
        }

        fun toJpo(domain: OrderSheetReceipt): OrderSheetReceiptJpo {
            return OrderSheetReceiptJpo(
                    id = domain.id?.value,
                    orderSheet = OrderSheet.toJpo(domain.orderSheet),
                    fileName = domain.fileName,
                    filePath = domain.filePath,
                    fileFormat = domain.fileFormat,
                    metadata = domain.metadata
            )
        }
    }
}
