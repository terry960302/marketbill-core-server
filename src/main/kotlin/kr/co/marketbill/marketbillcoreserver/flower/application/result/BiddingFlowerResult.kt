package kr.co.marketbill.marketbillcoreserver.flower.application.result

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import java.time.LocalDate
import java.time.LocalDateTime

data class BiddingFlowerResult(
    val id: Long,
    val flower: FlowerResult?,
    val biddingDate: LocalDateTime
) {

    companion object {
        fun from(domain: BiddingFlower): BiddingFlowerResult {
            return BiddingFlowerResult(
                id = domain.id!!.value,
                flower = domain.flower?.let { FlowerResult.from(domain.flower) },
                biddingDate = domain.biddingDate
            )
        }
    }
}