package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class UserName(val value: String) {
    init {
        require(value.isNotBlank()) { "사용자 이름은 비어있을 수 없습니다." }
        require(value.length <= 50) { "사용자 이름은 50자를 초과할 수 없습니다." }
    }

    companion object {
        fun from(value: String): UserName = UserName(value.trim())
    }
}
