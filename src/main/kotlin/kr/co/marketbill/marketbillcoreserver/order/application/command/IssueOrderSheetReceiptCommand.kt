package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId

data class IssueOrderSheetReceiptCommand(val orderSheetId: Long) {
    companion object{
        fun from(orderSheetId: Int) : IssueOrderSheetReceiptCommand{
            return IssueOrderSheetReceiptCommand(orderSheetId.toLong())
        }
    }
}
