package kr.co.marketbill.marketbillcoreserver.cart.domain.vo

@JvmInline
value class ShoppingSessionId(val value: Long) {
    init {
        require(value > 0) { "ShoppingSessionId는 0보다 커야 합니다." }
    }
}
