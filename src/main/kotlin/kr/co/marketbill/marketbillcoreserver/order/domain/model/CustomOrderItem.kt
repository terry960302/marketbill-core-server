package kr.co.marketbill.marketbillcoreserver.order.domain.model

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.CustomOrderItemJpo
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.CustomOrderItemId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User

data class CustomOrderItem(
        val id: CustomOrderItemId?,
        val orderSheet: OrderSheet,
        val retailer: User?,
        val wholesaler: User?,
        val flowerName: String?,
        val flowerTypeName: String?,
        val quantity: Quantity?,
        val grade: FlowerGrade?,
        val price: Int?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    init {
        require(price == null || price >= 0) { "가격은 0 이상이어야 합니다" }
        require(flowerName?.isNotBlank() != false || flowerTypeName?.isNotBlank() != false) {
            "꽃 이름 또는 꽃 타입 이름 중 하나는 필수입니다"
        }
    }

    fun calculateTotalPrice(): Int = (price ?: 0) * (quantity?.value ?: 0)

    fun updatePrice(newPrice: Int): CustomOrderItem {
        require(newPrice >= 0) { "가격은 0 이상이어야 합니다" }
        return copy(price = newPrice)
    }

    companion object {
        fun fromJpo(jpo: CustomOrderItemJpo, orderSheet: OrderSheet? = null): CustomOrderItem {
            return CustomOrderItem(
                    id = jpo.id?.let { CustomOrderItemId.from(it) },
                    orderSheet = orderSheet ?: OrderSheet.fromJpo(jpo.orderSheet),
                    retailer = jpo.retailer?.let { User.fromJpo(it) },
                    wholesaler = jpo.wholesaler?.let { User.fromJpo(it) },
                    flowerName = jpo.flowerName,
                    flowerTypeName = jpo.flowerTypeName,
                    quantity = jpo.quantity?.let { Quantity.from(it) },
                    grade = jpo.grade?.let { FlowerGrade.valueOf(it.name) },
                    price = jpo.price,
                    createdAt = jpo.createdAt,
                    updatedAt = jpo.updatedAt,
                    deletedAt = jpo.deletedAt
            )
        }

        fun toJpo(domain: CustomOrderItem): CustomOrderItemJpo {
            return CustomOrderItemJpo(
                    id = domain.id?.value,
                    orderSheet = OrderSheet.toJpo(domain.orderSheet),
                    retailer = domain.retailer?.let { User.toJpo(it) },
                    wholesaler = domain.wholesaler?.let { User.toJpo(it) },
                    flowerName = domain.flowerName,
                    flowerTypeName = domain.flowerTypeName,
                    quantity = domain.quantity?.value,
                    grade =
                            domain.grade?.let {
                                kr.co.marketbill.marketbillcoreserver.types.FlowerGrade.valueOf(
                                        it.name
                                )
                            },
                    price = domain.price
            )
        }
    }
}
