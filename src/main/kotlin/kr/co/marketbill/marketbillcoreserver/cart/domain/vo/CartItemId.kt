package kr.co.marketbill.marketbillcoreserver.cart.domain.vo

@JvmInline
value class CartItemId(val value: Long) {
    init {
        require(value > 0) { "CartItemId는 0보다 커야 합니다." }
    }
}
