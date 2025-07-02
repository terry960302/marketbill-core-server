package kr.co.marketbill.marketbillcoreserver.flower.domain.vo

@JvmInline
value class FlowerTypeId(val value: Long) {
    init {
        require(value > 0) { "FlowerTypeId는 양수여야 합니다" }
    }
}