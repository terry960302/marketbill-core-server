package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "bidding_flowers")
data class BiddingFlower(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "flower_id")
    val flower: Flower? = null,

    @Column(name = "bidding_date")
    val biddingDate: LocalDateTime = LocalDateTime.now(),
) : SoftDeleteEntity() {
}