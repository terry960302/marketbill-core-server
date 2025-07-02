package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerColorJpo
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerColorId

data class FlowerColor(
    val id: FlowerColorId?,
    val name: String,
) {
    init {
        require(name.isNotBlank()) { "꽃색깔 명칭은 필수입니다." }
    }

    companion object {
        fun from(jpo: FlowerColorJpo): FlowerColor {
            return FlowerColor(
                id = jpo.id?.let { FlowerColorId(it) },
                name = jpo.name,
            )
        }
    }
}