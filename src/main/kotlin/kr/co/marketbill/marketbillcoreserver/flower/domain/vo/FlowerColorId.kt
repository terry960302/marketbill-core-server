package kr.co.marketbill.marketbillcoreserver.flower.domain.vo

@JvmInline
value class FlowerColorId(val value: Long) {
    init {
        require(value > 0) { "Flower color Id는 양수여야 합니다" }
    }
}