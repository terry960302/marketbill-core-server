package kr.co.marketbill.marketbillcoreserver.flower.application.result

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import java.time.LocalDate

data class BiddingFlowerResult(
    val id: Long,
    val flower: FlowerResult,
    val biddingDate: LocalDate
) {

    companion object {
        fun from(domain: BiddingFlower): BiddingFlowerResult {
            return BiddingFlowerResult(
                id = domain.id.value,
                flower = FlowerResult.from(domain.flower),
                biddingDate = domain.biddingDate
            )
        }
    }
}