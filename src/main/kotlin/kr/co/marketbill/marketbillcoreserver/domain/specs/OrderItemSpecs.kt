package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class OrderItemSpecs {
    companion object {
        fun byOrderSheetId(orderSheetId: Long?): Specification<OrderItem> {
            return Specification<OrderItem> { root, query, builder ->
                if (orderSheetId == null) {
                    builder.conjunction()
                } else {
                    val orderSheet = root.join<OrderItem, OrderSheet>("orderSheet")
                    builder.equal(orderSheet.get<Long>("id"), orderSheetId)
                }

            }
        }

        fun byOrderSheetIds(orderSheetIds: List<Long>): Specification<OrderItem> {
            return Specification<OrderItem> { root, query, builder ->
                val orderSheet = root.join<OrderItem, OrderSheet>("orderSheet")
                orderSheet.get<Long>("id").`in`(orderSheetIds)
            }
        }

    }
}