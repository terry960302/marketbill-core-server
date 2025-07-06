package kr.co.marketbill.marketbillcoreserver.order.domain.vo

@JvmInline
value class OrderItemId(val value: Long) {
    init {
        require(value > 0) { "OrderItemId는 양수여야 합니다" }
    }

    companion object {
        fun from(value: Long): OrderItemId {
            return OrderItemId(value)
        }
    }
}
