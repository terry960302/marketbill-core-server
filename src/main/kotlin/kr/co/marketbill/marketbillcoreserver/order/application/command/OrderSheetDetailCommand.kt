package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId

data class OrderSheetDetailCommand(val orderSheetId: Int) {
    fun toOrderSheetId(): OrderSheetId {
        return OrderSheetId.from(orderSheetId.toLong())
    }
}
