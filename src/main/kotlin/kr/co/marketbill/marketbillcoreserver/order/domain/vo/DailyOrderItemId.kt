package kr.co.marketbill.marketbillcoreserver.order.domain.vo

@JvmInline
value class DailyOrderItemId(val value: Long) {
    init {
        require(value > 0) { "DailyOrderItemId는 양수여야 합니다" }
    }

    companion object {
        fun from(value: Long): DailyOrderItemId {
            return DailyOrderItemId(value)
        }
    }
}
