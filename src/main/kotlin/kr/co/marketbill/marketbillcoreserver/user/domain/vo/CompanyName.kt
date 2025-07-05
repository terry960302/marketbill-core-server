package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class CompanyName(val value: String) {
    init {
        require(value.isNotBlank()) { "회사명은 비어 있을 수 없습니다." }
        require(value.length <= 100) { "회사명은 100자 이하여야 합니다." }
    }

    companion object {
        fun from(value: String): CompanyName = CompanyName(value)
    }
}
