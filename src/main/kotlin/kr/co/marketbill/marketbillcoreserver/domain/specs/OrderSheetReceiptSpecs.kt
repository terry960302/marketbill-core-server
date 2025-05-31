package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class OrderSheetReceiptSpecs {
    companion object {
        fun byOrderSheetIds(orderSheetIds: List<Long>): Specification<OrderSheetReceipt> {
            return Specification<OrderSheetReceipt> { root, query, builder ->
                val orderSheet = root.join<OrderSheetReceipt, OrderSheet>("orderSheet")
                orderSheet.get<Long>("id").`in`(orderSheetIds)
            }
        }
    }
}