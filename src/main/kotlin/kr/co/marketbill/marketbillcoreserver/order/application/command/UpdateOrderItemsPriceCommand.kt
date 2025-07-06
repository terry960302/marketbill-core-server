package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderItemId
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput

data class UpdateOrderItemsPriceCommand(val items: List<OrderItemPriceCommand>) {
    companion object {
        fun from(items: List<OrderItemPriceInput>): UpdateOrderItemsPriceCommand {
            return UpdateOrderItemsPriceCommand(items = items.map { OrderItemPriceCommand.from(it.id, it.price) })
        }
    }
}
