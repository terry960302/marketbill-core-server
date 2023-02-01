package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "carts")
@SQLDelete(sql = "UPDATE carts SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null AND ordered_at is Null")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    var retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id", nullable = true)
    var wholesaler: User? = null,

    @Column(name = "memo", nullable = true)
    val memo: String? = null,

    @Column(name = "ordered_at")
    var orderedAt: LocalDateTime? = null,
) : BaseTime() {
    @PostLoad
    @PostUpdate
    fun postLoad() {
        retailer = if (retailer?.deletedAt == null) retailer else null
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
    }
}