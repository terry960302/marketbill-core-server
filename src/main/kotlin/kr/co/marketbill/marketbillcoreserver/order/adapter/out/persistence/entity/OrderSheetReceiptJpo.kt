package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo

@Entity
@Table(
        name = "order_sheet_receipts",
        indexes =
                [
                        Index(
                                name = "idx_order_sheet_receipt_created_at",
                                columnList = "created_at"
                        ),
                        Index(
                                name = "idx_order_sheet_receipt_order_sheet_id",
                                columnList = "order_sheet_id"
                        )]
)
class OrderSheetReceiptJpo(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_sheet_id", nullable = false)
        var orderSheet: OrderSheetJpo,
        @Column(name = "file_name", nullable = false, length = 255) var fileName: String,
        @Column(name = "file_path", nullable = false, length = 500) var filePath: String,
        @Column(name = "file_format", nullable = false, length = 10) var fileFormat: String,
        @Column(name = "metadata", nullable = false, length = 1000) var metadata: String
) : BaseJpo() {

    companion object {
        fun create(
                orderSheet: OrderSheetJpo,
                fileName: String,
                filePath: String,
                fileFormat: String,
                metadata: String
        ): OrderSheetReceiptJpo {
            return OrderSheetReceiptJpo(
                    orderSheet = orderSheet,
                    fileName = fileName,
                    filePath = filePath,
                    fileFormat = fileFormat,
                    metadata = metadata
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderSheetReceiptJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
