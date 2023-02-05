package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CustomOrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class CustomOrderItemSpecs {
    companion object {
        fun byOrderSheetIds(orderSheetIds: List<Long>): Specification<CustomOrderItem> {
            return Specification<CustomOrderItem> { root, query, builder ->
                if (orderSheetIds.isEmpty()) {
                    builder.conjunction()
                } else {
                    val orderSheet = root.join<CustomOrderItem, OrderSheet>("orderSheet")
                    orderSheet.get<Long>("id").`in`(orderSheetIds)
                }
            }
        }
    }
}