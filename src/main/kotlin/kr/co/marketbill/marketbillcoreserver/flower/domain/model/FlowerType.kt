package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerTypeJpo
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

    companion object {
        fun fromJpo(jpo: FlowerTypeJpo): FlowerType = FlowerType(
            id = jpo.id?.let { FlowerTypeId(it) },
            name = jpo.name,
            imgUrl = jpo.imgUrl
        )

        fun toJpo(domain : FlowerType) : FlowerTypeJpo  {
            return FlowerTypeJpo(
                id = domain.id?.value,
                name = domain.name,
                imgUrl = domain.imgUrl
            )
        }
    }
}


