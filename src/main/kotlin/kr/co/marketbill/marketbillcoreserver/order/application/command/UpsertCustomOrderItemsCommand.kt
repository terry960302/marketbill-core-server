package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.types.UpsertCustomOrderItemsInput

data class UpsertCustomOrderItemsCommand(val orderSheetId: Long, val items: List<CustomOrderItemCommand>) {
    companion object {
        fun from(input: UpsertCustomOrderItemsInput): UpsertCustomOrderItemsCommand {
            return UpsertCustomOrderItemsCommand(
                orderSheetId = input.orderSheetId.toLong(),
                items = input.items.map { CustomOrderItemCommand.from(it) }
            )
        }
    }
}