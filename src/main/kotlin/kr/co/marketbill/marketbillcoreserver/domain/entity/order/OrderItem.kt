package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter
import org.hibernate.annotations.FilterJoinTable
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.hibernate.annotations.WhereJoinTable
import javax.persistence.*

@Entity
@Table(name = "order_items")
@SQLDelete(sql = "UPDATE order_items SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "order_sheet_id")
    val orderSheet: OrderSheet? = null,

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    var retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id")
    var wholesaler: User? = null,

    @ManyToOne
    @JoinColumn(name = "flower_id")
    val flower: Flower? = null,

    @Column(name = "quantity")
    val quantity: Int? = null,

    @Column(name = "grade")
    val grade: String? = null,

    @Transient
    var gradeValue: FlowerGrade? = null,

    @Column(name = "price", nullable = true)
    var price: Int? = null,
) : BaseTime() {
    @PostLoad
    fun postLoad() {
        gradeValue = EnumConverter.convertFlowerGradeKorToEnum(grade!!)
        retailer = if (retailer?.deletedAt == null) retailer else null
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
    }
}
