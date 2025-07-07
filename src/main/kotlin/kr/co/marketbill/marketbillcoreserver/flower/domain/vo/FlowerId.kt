package kr.co.marketbill.marketbillcoreserver.flower.domain.vo

@JvmInline
value class FlowerId(val value: Long) {
    init {
        require(value > 0) { "FlowerId는 양수여야 합니다" }
    }
}