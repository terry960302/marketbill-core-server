package kr.co.marketbill.marketbillcoreserver.order.application.result

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.domain.model.CustomOrderItem
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetResult
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult

data class CustomOrderItemResult(
        val id: Long,
        val orderSheet: OrderSheetResult,
        val retailer: UserResult?,
        val wholesaler: UserResult?,
        val flowerName: String?,
        val flowerTypeName: String?,
        val quantity: Int?,
        val grade: String?,
        val price: Int?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    companion object {
        fun from(customOrderItem: CustomOrderItem): CustomOrderItemResult {
            return CustomOrderItemResult(
                    id = customOrderItem.id?.value ?: 0L,
                    orderSheet = OrderSheetResult.from(customOrderItem.orderSheet),
                    retailer = customOrderItem.retailer?.let { UserResult.from(it) },
                    wholesaler = customOrderItem.wholesaler?.let { UserResult.from(it) },
                    flowerName = customOrderItem.flowerName,
                    flowerTypeName = customOrderItem.flowerTypeName,
                    quantity = customOrderItem.quantity?.value,
                    grade = customOrderItem.grade?.name,
                    price = customOrderItem.price,
                    createdAt = customOrderItem.createdAt,
                    updatedAt = customOrderItem.updatedAt,
                    deletedAt = customOrderItem.deletedAt
            )
        }
    }
}
