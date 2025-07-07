package kr.co.marketbill.marketbillcoreserver.legacy.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import javax.persistence.*

@Entity
@Table(name = "batch_cart_to_order_logs")
data class BatchCartToOrderLogs(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "cart_item_count")
    var cartItemsCount: Int? = null,

    @Column(name = "order_sheet_count")
    var orderSheetCount: Int? = null,

    @Column(name = "order_item_count")
    var orderItemCount: Int? = null,

    @Column(name = "err_logs", columnDefinition = "TEXT")
    var errLogs: String? = null

) : BaseTime() {
}