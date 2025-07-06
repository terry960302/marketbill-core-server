package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.order.domain.vo.DailyOrderItemId
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput

data class UpdateDailyOrderItemsPriceCommand(val items: List<DailyOrderItemPriceCommand>) {
    companion object {
        fun from(items: List<OrderItemPriceInput>): UpdateDailyOrderItemsPriceCommand {
            return UpdateDailyOrderItemsPriceCommand(items.map { DailyOrderItemPriceCommand.from(it) })
        }
    }
}
