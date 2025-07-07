package kr.co.marketbill.marketbillcoreserver.order.domain.vo

@JvmInline
value class OrderSheetReceiptId(val value: Long) {
    init {
        require(value > 0) { "OrderSheetReceiptId는 양수여야 합니다" }
    }

    companion object {
        fun from(value: Long): OrderSheetReceiptId {
            return OrderSheetReceiptId(value)
        }
    }
}
