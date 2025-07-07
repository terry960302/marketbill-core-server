package kr.co.marketbill.marketbillcoreserver.cart.domain.vo

@JvmInline
value class Quantity(val value: Int) {
    init {
        require(value > 0) { "수량은 0보다 커야 합니다." }
    }


}
