package kr.co.marketbill.marketbillcoreserver.order.application.result

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheet
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderItemResult

data class OrderSheetResult(
        val id: Long,
        val orderNo: String,
        val retailer: UserResult?,
        val wholesaler: UserResult?,
        val orderItems: List<OrderItemResult>,
        val customOrderItems: List<CustomOrderItemResult>,
        val totalFlowerQuantity: Int,
        val totalFlowerTypeCount: Int,
        val totalFlowerPrice: Int,
        val hasReceipt: Boolean,
        val orderSheetReceipts: List<OrderSheetReceiptResult>,
        val recentReceipt: OrderSheetReceiptResult?,
        val isPriceUpdated: Boolean?,
        val memo: String?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    companion object {
        fun from(orderSheet: OrderSheet): OrderSheetResult {
            return OrderSheetResult(
                    id = orderSheet.id?.value ?: 0L,
                    orderNo = orderSheet.orderNo,
                    retailer = orderSheet.retailer?.let { UserResult.from(it) },
                    wholesaler = orderSheet.wholesaler?.let { UserResult.from(it) },
                    orderItems = orderSheet.orderItems.map { OrderItemResult.from(it) },
                    customOrderItems =
                            orderSheet.customOrderItems.map { CustomOrderItemResult.from(it) },
                    totalFlowerQuantity = orderSheet.totalFlowerQuantity,
                    totalFlowerTypeCount = orderSheet.totalFlowerTypeCount,
                    totalFlowerPrice = orderSheet.totalFlowerPrice,
                    hasReceipt = orderSheet.hasReceipt,
                    orderSheetReceipts =
                            orderSheet.orderSheetReceipts.map { OrderSheetReceiptResult.from(it) },
                    recentReceipt =
                            orderSheet.recentReceipt?.let { OrderSheetReceiptResult.from(it) },
                    isPriceUpdated = orderSheet.isPriceUpdated,
                    memo = orderSheet.memo,
                    createdAt = orderSheet.createdAt,
                    updatedAt = orderSheet.updatedAt,
                    deletedAt = orderSheet.deletedAt
            )
        }
    }
}
