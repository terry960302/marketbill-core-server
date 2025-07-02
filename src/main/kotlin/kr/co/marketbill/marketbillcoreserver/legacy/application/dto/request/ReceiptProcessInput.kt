package kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request

data class ReceiptProcessInput(
    val orderNo: String,
    val retailer: kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.Retailer,
    val wholesaler: kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.Wholesaler,
    val orderItems: List<kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.OrderItem>
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
        val flower: kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.Flower,
        val quantity: Int,
        val grade: String,
        val price: Int?,
    )

    data class Flower(val name: String, val flowerType: kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.ReceiptProcessInput.FlowerType)
    data class FlowerType(val name: String)

}

