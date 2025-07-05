package kr.co.marketbill.marketbillcoreserver.flower.application.result

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import java.time.LocalDateTime

data class FlowerResult(
    val id: Long,
    val name: String,
    val flowerTypeName: String,
    val flowerTypeImgUrl: String?,
    val flowerImages: List<String>,
    val flowerColor: FlowerColorResult?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(flower: Flower): FlowerResult {
            return FlowerResult(
                id = flower.id?.value ?: 0L,
                name = flower.name,
                flowerTypeName = flower.type.name,
                flowerTypeImgUrl = flower.type.imgUrl,
                flowerImages = flower.images.map { it.value },
                flowerColor = flower.color?.let { FlowerColorResult.from(it) },
                createdAt = flower.createdAt,
            )
        }
    }
}
