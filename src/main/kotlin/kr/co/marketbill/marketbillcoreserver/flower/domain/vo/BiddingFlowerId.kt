package kr.co.marketbill.marketbillcoreserver.flower.domain.vo

@JvmInline
value class BiddingFlowerId(val value: Long) {
    init {
        require(value >= 0) { "BiddingFlower Id는 0 이상이어야 합니다." }
    }
}