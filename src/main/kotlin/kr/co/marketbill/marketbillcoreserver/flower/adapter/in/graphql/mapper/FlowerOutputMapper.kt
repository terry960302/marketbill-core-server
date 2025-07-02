package kr.co.marketbill.marketbillcoreserver.flower.adapter.`in`.graphql.mapper

import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerResult
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerSearchResult
import kr.co.marketbill.marketbillcoreserver.types.*

object FlowerOutputMapper {
    fun mapToFlowersOutput(result: FlowerSearchResult): FlowersOutput {
        return FlowersOutput(
            resultCount = result.flowers.size,
            items = result.flowers.map { mapToFlower(it) }
        )
    }

    private fun mapToFlower(item: FlowerResult): Flower {
        return Flower(
            id = item.id.toInt(),
            flowerType = FlowerType(
                id = item.id.toInt(),
                name = item.flowerTypeName,
                imgUrl = item.flowerTypeImgUrl
            ),
            name = item.name,
            images = item.flowerImages,
            flowerColor = item.flowerColor?.let {
                FlowerColor(
                    id = item.flowerColor.id.toInt(),
                    name = item.flowerColor.name
                )
            },
            biddingFlowers = item.biddingFlowers.map {
                BiddingFlower(
                    id = it.id.toInt(),
                    biddingDate = it.biddingDate,
                    flower = null,
                )
            },
        )
    }
}