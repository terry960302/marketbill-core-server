package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class BusinessMainCategory(val value: String) {
    init {
        require(value.isNotBlank()) { "업종 대분류는 비어 있을 수 없습니다." }
        require(value.length <= 50) { "업종 대분류는 50자 이하여야 합니다." }
    }

    companion object {
        fun from(value: String): BusinessMainCategory = BusinessMainCategory(value)
    }
}
