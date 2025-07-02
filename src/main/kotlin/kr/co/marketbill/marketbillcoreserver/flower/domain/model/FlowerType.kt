package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerTypeId

data class FlowerType(
    val id: FlowerTypeId?,
    val name: String,
    val imgUrl: String?
) {
    init {
        require(name.isNotBlank()) { "꽃 타입 이름은 필수입니다" }
    }

    fun matchesKeyword(keyword: String): Boolean = name.contains(keyword, ignoreCase = true)
}


