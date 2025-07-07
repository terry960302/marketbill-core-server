package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId

data class RemoveOrderSheetCommand(val orderSheetId: Long) {
    companion object{
        fun from(orderSheetId: Int) : RemoveOrderSheetCommand{
            return RemoveOrderSheetCommand(orderSheetId.toLong())
        }
    }
}
