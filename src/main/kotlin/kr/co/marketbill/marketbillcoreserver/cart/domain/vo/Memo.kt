package kr.co.marketbill.marketbillcoreserver.cart.domain.vo

@JvmInline
value class Memo(val value: String) {
    init {
        require(value.length <= 500) { "메모는 500자를 초과할 수 없습니다." }
    }
}
