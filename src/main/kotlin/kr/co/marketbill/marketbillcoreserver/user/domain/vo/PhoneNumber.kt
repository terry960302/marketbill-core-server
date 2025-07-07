package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "전화번호는 비어있을 수 없습니다." }
        require(value.matches(Regex("^01[0-9]-?[0-9]{4}-?[0-9]{4}$"))) { 
            "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)" 
        }
    }

    companion object {
        fun from(value: String): PhoneNumber = PhoneNumber(value.replace("-", ""))
    }
} 