package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerJpo
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo

@Entity
@Table(
        name = "daily_order_items",
        indexes =
                [
                        Index(name = "idx_daily_order_item_created_at", columnList = "created_at"),
                        Index(
                                name = "idx_daily_order_item_wholesaler_id",
                                columnList = "wholesaler_id"
                        ),
                        Index(name = "idx_daily_order_item_flower_id", columnList = "flower_id")]
)
class DailyOrderItemJpo(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wholesaler_id")
        var wholesaler: UserJpo?,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "flower_id", nullable = false)
        var flower: FlowerJpo,
        @Enumerated(EnumType.STRING)
        @Column(name = "grade", nullable = false)
        var grade: kr.co.marketbill.marketbillcoreserver.types.FlowerGrade,
        @Column(name = "price") var price: Int? = null
) : BaseJpo() {

    companion object {
        fun create(
                wholesaler: UserJpo?,
                flower: FlowerJpo,
                grade: kr.co.marketbill.marketbillcoreserver.types.FlowerGrade,
                price: Int? = null
        ): DailyOrderItemJpo {
            return DailyOrderItemJpo(
                    wholesaler = wholesaler,
                    flower = flower,
                    grade = grade,
                    price = price
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DailyOrderItemJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
