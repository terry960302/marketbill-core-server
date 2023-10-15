package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

data class AuctionResultFlowerGradeInfo(
    val id: Long,
    val flowerGrade: String
)

data class AuctionResultWithGroupBy(
    val id: Long,
    val flowerName: String,
    val flowerTypeName: String,
    val images: List<String> = emptyList(),
    val auctionDate: Int,
    val flowerGradeInfos: List<AuctionResultFlowerGradeInfo>,
    val retailPrice: Int
) {
    companion object {
        fun of(
            value: InterfaceAuctionResultWithGroupBy,
            images: List<String>
        ): AuctionResultWithGroupBy {
            return AuctionResultWithGroupBy(
                id = value.id,
                flowerName = value.flowerName,
                flowerTypeName = value.flowerTypeName,
                images = images,
                auctionDate = value.auctionDate,
                flowerGradeInfos = value.flowerGrade.split(",").map {
                    val split = it.split("|")
                    AuctionResultFlowerGradeInfo(
                        id = split[0].toLong(),
                        flowerGrade = split[1]
                    )
                },
                retailPrice = value.retailPrice
            )
        }
    }
}