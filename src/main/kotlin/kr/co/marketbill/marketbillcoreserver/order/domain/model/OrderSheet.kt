package kr.co.marketbill.marketbillcoreserver.order.domain.model

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.OrderSheetJpo
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User

data class OrderSheet(
        val id: OrderSheetId?,
        val orderNo: String,
        val retailer: User?,
        val wholesaler: User?,
        val orderItems: List<OrderItem>,
        val customOrderItems: List<CustomOrderItem>,
        val totalFlowerQuantity: Int,
        val totalFlowerTypeCount: Int,
        val totalFlowerPrice: Int,
        val hasReceipt: Boolean,
        val orderSheetReceipts: List<OrderSheetReceipt>,
        val recentReceipt: OrderSheetReceipt?,
        val isPriceUpdated: Boolean?,
        val memo: String?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    init {
        require(orderNo.isNotBlank()) { "주문번호는 필수입니다" }
        require(totalFlowerQuantity >= 0) { "총 꽃 개수는 0 이상이어야 합니다" }
        require(totalFlowerTypeCount >= 0) { "총 꽃 품종 개수는 0 이상이어야 합니다" }
        require(totalFlowerPrice >= 0) { "총 꽃 가격은 0 이상이어야 합니다" }
    }

    fun hasItems(): Boolean = orderItems.isNotEmpty() || customOrderItems.isNotEmpty()

    fun canBeDeleted(): Boolean = !hasReceipt

    fun calculateTotalQuantity(): Int =
            orderItems.sumOf { it.quantity.value } +
                    customOrderItems.sumOf { it.quantity?.value ?: 0 }

    fun calculateTotalPrice(): Int =
            orderItems.sumOf { it.price ?: 0 } + customOrderItems.sumOf { it.price ?: 0 }

    companion object {
        fun fromJpo(jpo: OrderSheetJpo): OrderSheet {
            return OrderSheet(
                    id = jpo.id?.let { OrderSheetId.from(it) },
                    orderNo = jpo.orderNo,
                    retailer = jpo.retailer?.let { User.fromJpo(it) },
                    wholesaler = jpo.wholesaler?.let { User.fromJpo(it) },
                    orderItems = jpo.orderItems.map { OrderItem.fromJpo(it, null) },
                    customOrderItems =
                            jpo.customOrderItems.map { CustomOrderItem.fromJpo(it, null) },
                    totalFlowerQuantity = jpo.totalFlowerQuantity,
                    totalFlowerTypeCount = jpo.totalFlowerTypeCount,
                    totalFlowerPrice = jpo.totalFlowerPrice,
                    hasReceipt = jpo.hasReceipt,
                    orderSheetReceipts =
                            jpo.orderSheetReceipts.map { OrderSheetReceipt.fromJpo(it, null) },
                    recentReceipt =
                            jpo.orderSheetReceipts.maxByOrNull { it.createdAt }?.let {
                                OrderSheetReceipt.fromJpo(it, null)
                            },
                    isPriceUpdated = jpo.isPriceUpdated,
                    memo = jpo.memo,
                    createdAt = jpo.createdAt,
                    updatedAt = jpo.updatedAt,
                    deletedAt = jpo.deletedAt
            )
        }

        fun toJpo(domain: OrderSheet): OrderSheetJpo {
            return OrderSheetJpo(
                    id = domain.id?.value,
                    orderNo = domain.orderNo,
                    retailer = domain.retailer?.let { User.toJpo(it) },
                    wholesaler = domain.wholesaler?.let { User.toJpo(it) },
                    totalFlowerQuantity = domain.totalFlowerQuantity,
                    totalFlowerTypeCount = domain.totalFlowerTypeCount,
                    totalFlowerPrice = domain.totalFlowerPrice,
                    hasReceipt = domain.hasReceipt,
                    isPriceUpdated = domain.isPriceUpdated,
                    memo = domain.memo
            )
        }
    }
}
