package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class CompanyPhoneNo(val value: String) {
    init {
        require(Regex("^\\d{2,4}-\\d{3,4}-\\d{4}$").matches(value)) { "유효하지 않은 전화번호 형식입니다." }
    }

    companion object {
        fun from(value: String): CompanyPhoneNo = CompanyPhoneNo(value)
    }
}
