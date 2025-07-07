package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class BusinessNo(val value: String) {
    init {
        require(value.length == 10) { "사업자번호는 10자리여야 합니다." }
    }

    companion object {
        fun from(value: String): BusinessNo = BusinessNo(value)
    }
}
