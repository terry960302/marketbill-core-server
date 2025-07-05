package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class Address(val value: String) {
    init {
        require(value.isNotBlank()) { "주소는 비어 있을 수 없습니다." }
        require(value.length <= 200) { "주소는 200자 이하여야 합니다." }
    }

    companion object {
        fun from(value: String): Address = Address(value)
    }
}
