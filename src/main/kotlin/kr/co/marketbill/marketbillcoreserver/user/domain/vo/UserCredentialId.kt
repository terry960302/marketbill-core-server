package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class UserCredentialId(val value : Long) {
    init {
        require(value >= 0) {"UserCredentialId 는 0이상이어합니다."}
    }

    companion object{
        fun from(id : Long) : UserCredentialId{
            return UserCredentialId(id)
        }
    }
}