package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo

@Entity
@Table(
        name = "custom_order_items",
        indexes =
                [
                        Index(name = "idx_custom_order_item_created_at", columnList = "created_at"),
                        Index(
                                name = "idx_custom_order_item_order_sheet_id",
                                columnList = "order_sheet_id"
                        ),
                        Index(
                                name = "idx_custom_order_item_retailer_id",
                                columnList = "retailer_id"
                        ),
                        Index(
                                name = "idx_custom_order_item_wholesaler_id",
                                columnList = "wholesaler_id"
                        )]
)
class CustomOrderItemJpo(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_sheet_id", nullable = false)
        var orderSheet: OrderSheetJpo,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "retailer_id") var retailer: UserJpo?,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wholesaler_id")
        var wholesaler: UserJpo?,
        @Column(name = "flower_name", length = 100) var flowerName: String? = null,
        @Column(name = "flower_type_name", length = 100) var flowerTypeName: String? = null,
        @Column(name = "quantity") var quantity: Int? = null,
        @Enumerated(EnumType.STRING)
        @Column(name = "grade")
        var grade: kr.co.marketbill.marketbillcoreserver.types.FlowerGrade? = null,
        @Column(name = "price") var price: Int? = null
) : BaseJpo() {

    companion object {
        fun create(
                orderSheet: OrderSheetJpo,
                flowerName: String? = null,
                flowerTypeName: String? = null,
                quantity: Int? = null,
                grade: kr.co.marketbill.marketbillcoreserver.types.FlowerGrade? = null,
                retailer: UserJpo? = null,
                wholesaler: UserJpo? = null,
                price: Int? = null
        ): CustomOrderItemJpo {
            return CustomOrderItemJpo(
                    orderSheet = orderSheet,
                    flowerName = flowerName,
                    flowerTypeName = flowerTypeName,
                    quantity = quantity,
                    grade = grade,
                    retailer = retailer,
                    wholesaler = wholesaler,
                    price = price
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomOrderItemJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
