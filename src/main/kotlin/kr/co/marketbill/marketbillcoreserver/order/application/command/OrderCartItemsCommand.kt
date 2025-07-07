package kr.co.marketbill.marketbillcoreserver.order.application.command

data class OrderCartItemsCommand(
    val userId: Int? = null
) {
    companion object {
        fun from(userId: Int?): OrderCartItemsCommand {
            return OrderCartItemsCommand(userId)
        }
    }
}
