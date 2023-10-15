package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

interface InterfaceAuctionResultWithGroupBy {
    val id: Long
    val flowerName: String
    val flowerTypeName: String
    val auctionDate: Int
    val flowerGrade: String
    val retailPrice: Int
}