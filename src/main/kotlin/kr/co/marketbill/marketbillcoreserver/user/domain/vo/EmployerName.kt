package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class EmployerName private constructor(val value: String) {
    companion object {
        fun of(value: String): EmployerName {
            require(value.isNotBlank()) { "대표자명은 비어 있을 수 없습니다." }
            return EmployerName(value)
        }

        fun from(value: String): EmployerName = of(value)
    }
}
