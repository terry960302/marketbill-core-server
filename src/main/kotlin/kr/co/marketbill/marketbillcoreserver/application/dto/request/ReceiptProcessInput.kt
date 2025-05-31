package kr.co.marketbill.marketbillcoreserver.application.dto.request

data class ReceiptProcessInput(
    val orderNo: String,
    val retailer: Retailer,
    val wholesaler: Wholesaler,
    val orderItems: List<OrderItem>
) {
    data class Retailer(val name: String)

    data class Wholesaler(
        val businessNo: String,
        val companyName: String,
        val employerName: String,
        val sealStampImgUrl: String,
        val address: String,
        val companyPhoneNo: String,
        val businessMainCategory: String,
        val businessSubCategory: String,
        val bankAccount : String,
    )

    data class OrderItem(
        val flower: Flower,
        val quantity: Int,
        val grade: String,
        val price: Int?,
    )

    data class Flower(val name: String, val flowerType: FlowerType)
    data class FlowerType(val name: String)

}

