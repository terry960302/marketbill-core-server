package kr.co.marketbill.marketbillcoreserver.data.entity.flower

import kr.co.marketbill.marketbillcoreserver.data.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "bidding_flowers")
@SQLDelete(sql = "UPDATE bidding_flowers SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class BiddingFlower(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "flower_id")
    val flower: Flower? = null,

    @Column(name = "bidding_date")
    val biddingDate: LocalDateTime = LocalDateTime.now(),
) : BaseTime() {
}