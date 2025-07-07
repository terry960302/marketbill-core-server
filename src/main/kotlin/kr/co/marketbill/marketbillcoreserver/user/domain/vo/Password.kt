package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class Password(val value: String) {
    init {
        require(value.isNotBlank()) { "비밀번호는 비어있을 수 없습니다." }
        require(value.length >= 8) { "비밀번호는 최소 8자 이상이어야 합니다." }
        require(value.length <= 100) { "비밀번호는 100자를 초과할 수 없습니다." }
    }

    companion object {
        fun from(value: String): Password = Password(value)
    }
}
