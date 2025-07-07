package kr.co.marketbill.marketbillcoreserver.order.domain.vo

@JvmInline
value class OrderSheetId(val value: Long) {
    init {
        require(value > 0) { "OrderSheetId는 양수여야 합니다" }
    }

    companion object {
        fun from(value: Long): OrderSheetId {
            return OrderSheetId(value)
        }
    }
}
