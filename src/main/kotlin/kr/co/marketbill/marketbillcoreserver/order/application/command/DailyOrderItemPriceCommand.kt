package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput

class DailyOrderItemPriceCommand(val dailyOrderItemId: Long, val price: Int) {
    companion object {
        fun from(dailyOrderItemId: Int, price: Int): DailyOrderItemPriceCommand {
            return DailyOrderItemPriceCommand(dailyOrderItemId.toLong(), price)
        }

        fun from(input: OrderItemPriceInput): DailyOrderItemPriceCommand {
            return DailyOrderItemPriceCommand(input.id.toLong(), input.price)
        }
    }
}