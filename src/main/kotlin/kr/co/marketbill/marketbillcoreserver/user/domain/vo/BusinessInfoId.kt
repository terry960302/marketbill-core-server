package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class BusinessInfoId(val value : Long){
    init {
        require(value >= 0){"BusinessInfo의 ID는 0이상이어야 합니다."}
    }

    companion object{
        fun from(id : Long) : BusinessInfoId = BusinessInfoId(id)
    }
}