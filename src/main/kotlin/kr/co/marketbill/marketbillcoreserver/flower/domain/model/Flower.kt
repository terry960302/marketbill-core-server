package kr.co.marketbill.marketbillcoreserver.flower.domain.model

import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.*
import java.time.LocalDate
import java.time.LocalDateTime

data class Flower(
    val id: FlowerId?,
    val name: String,
    val type: FlowerType, // 품목
    val images: List<FlowerImageUrl>,
    val color: FlowerColor?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val biddingFlowers : List<BiddingFlower>
) {
    init {
        require(name.isNotBlank()) { "꽃 이름은 필수입니다" }
    }

    fun isCreatedAfter(date: LocalDate): Boolean = createdAt.toLocalDate().isAfter(date)
    fun isCreatedBefore(date: LocalDate): Boolean = createdAt.toLocalDate().isBefore(date)
    fun matchesKeyword(keyword: String): Boolean =
        name.contains(keyword, ignoreCase = true) || type.matchesKeyword(keyword)

    fun hasImages(): Boolean = images.isNotEmpty()
    fun hasBiddingFlowers() : Boolean = biddingFlowers.isNotEmpty()
}


