package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.BiddingFlowerId
import java.time.LocalDate

data class BiddingFlower(
    val id: BiddingFlowerId,
    val flower: Flower,
    val biddingDate: LocalDate,
) {
    init {
        requireNotNull(flower) { "flower 는 필수입니다." }
        requireNotNull(biddingDate) { "경매 날짜는 필수입니다." }
    }
}