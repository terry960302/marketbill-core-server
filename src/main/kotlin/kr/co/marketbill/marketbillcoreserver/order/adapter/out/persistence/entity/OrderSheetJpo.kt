package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo

@Entity
@Table(
        name = "order_sheets",
        indexes =
                [
                        Index(name = "idx_order_sheet_created_at", columnList = "created_at"),
                        Index(name = "idx_order_sheet_order_no", columnList = "order_no"),
                        Index(name = "idx_order_sheet_retailer_id", columnList = "retailer_id"),
                        Index(name = "idx_order_sheet_wholesaler_id", columnList = "wholesaler_id")]
)
class OrderSheetJpo(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @Column(name = "order_no", nullable = false, unique = true) var orderNo: String,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "retailer_id") var retailer: UserJpo?,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wholesaler_id")
        var wholesaler: UserJpo?,
        @Column(name = "total_flower_quantity", nullable = false) var totalFlowerQuantity: Int = 0,
        @Column(name = "total_flower_type_count", nullable = false)
        var totalFlowerTypeCount: Int = 0,
        @Column(name = "total_flower_price", nullable = false) var totalFlowerPrice: Int = 0,
        @Column(name = "has_receipt", nullable = false) var hasReceipt: Boolean = false,
        @Column(name = "is_price_updated") var isPriceUpdated: Boolean? = null,
        @Column(name = "memo", length = 1000) var memo: String? = null,
        @OneToMany(
                mappedBy = "orderSheet",
                fetch = FetchType.LAZY,
                cascade = [CascadeType.REMOVE],
                orphanRemoval = true
        )
        val orderItems: MutableList<OrderItemJpo> = mutableListOf(),
        @OneToMany(
                mappedBy = "orderSheet",
                fetch = FetchType.LAZY,
                cascade = [CascadeType.REMOVE],
                orphanRemoval = true
        )
        val customOrderItems: MutableList<CustomOrderItemJpo> = mutableListOf(),
        @OneToMany(
                mappedBy = "orderSheet",
                fetch = FetchType.LAZY,
                cascade = [CascadeType.REMOVE],
                orphanRemoval = true
        )
        val orderSheetReceipts: MutableList<OrderSheetReceiptJpo> = mutableListOf()
) : BaseJpo() {

    companion object {
        fun create(
                orderNo: String,
                retailer: UserJpo?,
                wholesaler: UserJpo?,
                memo: String? = null
        ): OrderSheetJpo {
            return OrderSheetJpo(
                    orderNo = orderNo,
                    retailer = retailer,
                    wholesaler = wholesaler,
                    memo = memo
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderSheetJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
