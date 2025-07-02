package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "bidding_flowers")
class BiddingFlowerJpo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "flower_id")
    var flower: FlowerJpo,

    @Column(name = "bidding_date")
    var biddingDate: LocalDateTime,
) : BaseJpo() {

    companion object {
        fun create(flower: FlowerJpo, biddingDate: LocalDateTime): BiddingFlowerJpo {
            return BiddingFlowerJpo(
                flower = flower,
                biddingDate = biddingDate
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BiddingFlowerJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}