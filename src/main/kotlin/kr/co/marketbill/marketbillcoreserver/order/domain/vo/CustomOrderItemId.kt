package kr.co.marketbill.marketbillcoreserver.order.domain.vo

@JvmInline
value class CustomOrderItemId(val value: Long) {
    init {
        require(value > 0) { "CustomOrderItemId는 양수여야 합니다" }
    }

    companion object {
        fun from(value: Long): CustomOrderItemId {
            return CustomOrderItemId(value)
        }
    }
}
