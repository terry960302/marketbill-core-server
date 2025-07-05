package kr.co.marketbill.marketbillcoreserver.legacy.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import javax.persistence.*

@Entity
@Table(name="order_sheet_receipts")
data class OrderSheetReceipt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "order_sheet_id")
    val orderSheet : OrderSheet? = null,

    @Column(name = "file_name")
    val fileName : String = "",

    @Column(name = "file_path")
    val filePath :String? = null,

    @Column(name="file_format")
    val fileFormat : String? = null,

    @Column(name= "metadata", columnDefinition = "TEXT")
    val metadata : String? = null,

    ) : SoftDeleteEntity()
