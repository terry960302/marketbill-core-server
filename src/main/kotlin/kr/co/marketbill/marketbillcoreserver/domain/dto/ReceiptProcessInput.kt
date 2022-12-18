package kr.co.marketbill.marketbillcoreserver.domain.dto

data class ReceiptProcessInput(
    val orderNo: String,
    val retailer: Username,
    val wholesaler: Username,
    val orderItems: List<OrderItem>
) {
    data class Username(val name: String)
    data class OrderItem(
        val flower: Flower,
        val quantity: Int,
        val grade: String,
        val price: Int,
    )
    data class Flower(val name: String, val flowerType: FlowerType)
    data class FlowerType(val name: String)

}

