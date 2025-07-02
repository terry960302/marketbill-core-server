package kr.co.marketbill.marketbillcoreserver.flower.domain.vo

@JvmInline
value class FlowerImageUrl(val value: String) {
    init {
        require(value.isNotBlank()) { "꽃 이미지 경로 주소는 필수입니다." }
    }

    companion object {
        fun from(imgUrl: String): FlowerImageUrl {
            return FlowerImageUrl(imgUrl)
        }
    }
}