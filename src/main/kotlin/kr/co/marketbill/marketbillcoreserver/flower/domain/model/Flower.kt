package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerColorJpo
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerJpo
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerTypeJpo
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.*
import java.time.LocalDate
import java.time.LocalDateTime

data class Flower(
    val id: FlowerId?,
    val name: String,
    val type: FlowerType, // 품목
    val images: List<FlowerImageUrl>,
    val color: FlowerColor,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    init {
        require(name.isNotBlank()) { "꽃 이름은 필수입니다" }
    }

    fun isCreatedAfter(date: LocalDate): Boolean = createdAt.toLocalDate().isAfter(date)
    fun isCreatedBefore(date: LocalDate): Boolean = createdAt.toLocalDate().isBefore(date)
    fun matchesKeyword(keyword: String): Boolean =
        name.contains(keyword, ignoreCase = true) || type.matchesKeyword(keyword)

    fun hasImages(): Boolean = images.isNotEmpty()

    companion object {
        fun fromJpo(jpo: FlowerJpo): Flower = Flower(
            id = jpo.id?.let { FlowerId(it) },
            name = jpo.name,
            type = FlowerType.fromJpo(jpo.flowerTypeJpo), // FlowerType에 fromJpo가 필요
            images = jpo.images.map { FlowerImageUrl(it) },
            color = FlowerColor.fromJpo(jpo.flowerColor), // FlowerColor에 fromJpo가 필요
            createdAt = jpo.createdAt,
            updatedAt = jpo.updatedAt,
        )

        fun toJpo(domain : Flower) : FlowerJpo{
            return FlowerJpo(
                id = domain.id?.value,
                name = domain.name,
                flowerTypeJpo = FlowerType.toJpo(domain.type),
                images = domain.images.map { it.value },
                flowerColor = domain.color.let { FlowerColor.toJpo(domain.color) },
            )
        }

    }


}


