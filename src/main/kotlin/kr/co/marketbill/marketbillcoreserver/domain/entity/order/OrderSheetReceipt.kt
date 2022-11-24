package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Table(name="order_sheet_receipts")
@SQLDelete(sql = "UPDATE order_sheet_receipts SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class OrderSheetReceipt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "order_sheet_id")
    val orderSheet : OrderSheet? = null,

    @Column(name = "file_path")
    val filePath :String? = null,

    @Column(name="file_format")
    val fileFormat : String? = null,

    @Column(name= "metadata", columnDefinition = "TEXT")
    val metadata : String? = null,

    ) : BaseTime()
