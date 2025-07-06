package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderItemId

data class OrderItemPriceCommand(val orderItemId: Long, val price: Int) {
    companion object {
        fun from(orderItemId: Int, price: Int): OrderItemPriceCommand {
            return OrderItemPriceCommand(orderItemId.toLong(), price)
        }
    }
}
