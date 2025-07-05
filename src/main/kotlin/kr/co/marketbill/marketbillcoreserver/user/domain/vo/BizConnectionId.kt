package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class BizConnectionId(val value : Long) {
    init {
        require(value >= 0){"BizConnectionId 은 0 이상이어야 합니다."}
    }

    companion object{
        fun from(id : Long) : BizConnectionId = BizConnectionId(id)
    }
}