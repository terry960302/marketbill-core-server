package kr.co.marketbill.marketbillcoreserver.legacy.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.shared.constants.FlowerGrade
import javax.persistence.*

@Entity
@Table(name = "daily_order_items")
data class DailyOrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id")
    var wholesaler: User? = null,

    @ManyToOne
    @JoinColumn(name = "flower_id")
    val flower: Flower? = null,

    @Column(name = "grade")
    val grade: String? = null,

    @Transient
    var gradeValue: FlowerGrade? = null,

    @Column(name = "price", nullable = true)
    var price: Int? = null,
) : SoftDeleteEntity() {
    @PostLoad
    @PostUpdate
    fun postLoad() {
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
    }
}
