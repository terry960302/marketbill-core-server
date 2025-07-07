package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.BiddingFlowerJpo
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.BiddingFlowerId
import java.time.LocalDate
import java.time.LocalDateTime

data class BiddingFlower(
    val id: BiddingFlowerId?,
    val flower: Flower?,
    val biddingDate: LocalDateTime,
) {
    init {
        requireNotNull(biddingDate) { "경매 날짜는 필수입니다." }
    }

    companion object {
        fun fromJpo(jpo: BiddingFlowerJpo): BiddingFlower {
            return BiddingFlower(
                id = BiddingFlowerId.from(jpo.id!!),
                flower = jpo.flower.let { Flower.fromJpo(jpo.flower) },
                biddingDate = jpo.biddingDate
            )
        }
    }
}