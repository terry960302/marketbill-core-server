package kr.co.marketbill.marketbillcoreserver.order.application.result

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderItem
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetResult
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerResult

data class OrderItemResult(
        val id: Long,
        val orderSheet: OrderSheetResult,
        val retailer: UserResult?,
        val wholesaler: UserResult?,
        val flower: FlowerResult,
        val quantity: Int,
        val grade: String,
        val price: Int?,
        val memo: String?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    companion object {
        fun from(orderItem: OrderItem): OrderItemResult {
            return OrderItemResult(
                    id = orderItem.id?.value ?: 0L,
                    orderSheet = OrderSheetResult.from(orderItem.orderSheet),
                    retailer = orderItem.retailer?.let { UserResult.from(it) },
                    wholesaler = orderItem.wholesaler?.let { UserResult.from(it) },
                    flower = FlowerResult.from(orderItem.flower),
                    quantity = orderItem.quantity.value,
                    grade = orderItem.grade.name,
                    price = orderItem.price,
                    memo = orderItem.memo,
                    createdAt = orderItem.createdAt,
                    updatedAt = orderItem.updatedAt,
                    deletedAt = orderItem.deletedAt
            )
        }
    }
}
