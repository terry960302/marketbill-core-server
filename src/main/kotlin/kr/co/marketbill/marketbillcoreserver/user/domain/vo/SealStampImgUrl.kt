package kr.co.marketbill.marketbillcoreserver.user.domain.vo

@JvmInline
value class SealStampImgUrl(val value: String) {
    init {
        require(value.isNotBlank()) { "인감 이미지 URL은 비어 있을 수 없습니다." }
        require(value.startsWith("http")) { "인감 이미지 URL은 http로 시작해야 합니다." }
        require(value.length <= 300) { "인감 이미지 URL은 300자 이하여야 합니다." }
    }

    companion object {
        fun from(value: String): SealStampImgUrl = SealStampImgUrl(value)
    }
}
