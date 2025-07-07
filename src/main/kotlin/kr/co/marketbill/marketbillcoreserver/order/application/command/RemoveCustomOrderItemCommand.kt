package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.order.domain.vo.CustomOrderItemId

data class RemoveCustomOrderItemCommand(val customOrderItemIds: List<Long>) {
    companion object {
        fun from(ids: List<Int>): RemoveCustomOrderItemCommand {
            return RemoveCustomOrderItemCommand(customOrderItemIds = ids.map { it.toLong() })
        }
    }
}
