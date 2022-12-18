package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.hibernate.annotations.WhereJoinTable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "cart_items")
@SQLDelete(sql = "UPDATE cart_items SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null AND ordered_at is Null")
data class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    var retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id", nullable = true)
    var wholesaler: User? = null,

    @ManyToOne
    @JoinColumn(name = "flower_id")
    val flower: Flower? = null,

    @Column(name = "quantity")
    var quantity: Int? = null,

    @Column(name = "grade")
    var grade: String? = null,

    @Transient
    var gradeValue : FlowerGrade? = null,

    @Column(name = "ordered_at")
    var orderedAt: LocalDateTime? = null,
) : BaseTime(){
    @PostLoad
    fun postLoad(){
        gradeValue = EnumConverter.convertFlowerGradeKorToEnum(grade!!)
        retailer = if (retailer?.deletedAt == null) retailer else null
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
    }

}
