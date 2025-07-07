package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerJpo
import javax.persistence.*

@Entity
@Table(
    name = "order_items",
    indexes = [
        Index(name = "idx_order_item_created_at", columnList = "created_at"),
        Index(name = "idx_order_item_order_sheet_id", columnList = "order_sheet_id"),
        Index(name = "idx_order_item_flower_id", columnList = "flower_id"),
        Index(name = "idx_order_item_retailer_id", columnList = "retailer_id"),
        Index(name = "idx_order_item_wholesaler_id", columnList = "wholesaler_id")
    ]
)
class OrderItemJpo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_sheet_id", nullable = false)
    var orderSheet: OrderSheetJpo,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_id")
    var retailer: UserJpo?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wholesaler_id")
    var wholesaler: UserJpo?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id", nullable = false)
    var flower: FlowerJpo,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    var grade: kr.co.marketbill.marketbillcoreserver.types.FlowerGrade,

    @Column(name = "price")
    var price: Int? = null,

    @Column(name = "memo", length = 1000)
    var memo: String? = null
) : BaseJpo() {

    companion object {
        fun create(
            orderSheet: OrderSheetJpo,
            flower: FlowerJpo,
            quantity: Int,
            grade: kr.co.marketbill.marketbillcoreserver.types.FlowerGrade,
            retailer: UserJpo? = null,
            wholesaler: UserJpo? = null,
            price: Int? = null,
            memo: String? = null
        ): OrderItemJpo {
            return OrderItemJpo(
                orderSheet = orderSheet,
                flower = flower,
                quantity = quantity,
                grade = grade,
                retailer = retailer,
                wholesaler = wholesaler,
                price = price,
                memo = memo
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderItemJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
} 