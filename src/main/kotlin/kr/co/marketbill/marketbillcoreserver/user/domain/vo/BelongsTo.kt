package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class BelongsTo(val value: String) {
    init {
        require(value.isNotBlank()) { "소속은 비어있을 수 없습니다." }
        require(value.length <= 100) { "소속은 100자를 초과할 수 없습니다." }
    }

    companion object {
        fun from(value: String): BelongsTo = BelongsTo(value.trim())
    }
}
