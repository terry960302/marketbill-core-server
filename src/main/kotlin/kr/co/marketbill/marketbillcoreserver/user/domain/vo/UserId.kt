package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class UserId(val value: Long) {
    init {
        require(value > 0) { "사용자 ID는 0보다 커야 합니다." }
    }

    companion object {
        fun from(value: Long): UserId = UserId(value)
    }
}
