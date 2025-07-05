package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class BankAccount(val value: String) {
    init {
        require(value.isNotBlank()) { "계좌번호는 비어 있을 수 없습니다." }
        require(value.length in 8..20) { "계좌번호는 8~20자여야 합니다." }
        require(value.all { it.isDigit() }) { "계좌번호는 숫자만 포함해야 합니다." }
    }

    companion object {
        fun from(value: String): BankAccount = BankAccount(value)
    }
}
