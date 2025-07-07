package kr.co.marketbill.marketbillcoreserver.order.domain.model

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.OrderItemJpo
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderItemId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User

data class OrderItem(
        val id: OrderItemId?,
        val orderSheet: OrderSheet,
        val retailer: User?,
        val wholesaler: User?,
        val flower: Flower,
        val quantity: Quantity,
        val grade: FlowerGrade,
        val price: Int?,
        val memo: String?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    init {
        require(price == null || price >= 0) { "가격은 0 이상이어야 합니다" }
    }

    fun calculateTotalPrice(): Int = (price ?: 0) * quantity.value

    fun updatePrice(newPrice: Int): OrderItem {
        require(newPrice >= 0) { "가격은 0 이상이어야 합니다" }
        return copy(price = newPrice)
    }

    companion object {
        fun fromJpo(jpo: OrderItemJpo, orderSheet: OrderSheet? = null): OrderItem {
            return OrderItem(
                    id = jpo.id?.let { OrderItemId.from(it) },
                    orderSheet = orderSheet ?: OrderSheet.fromJpo(jpo.orderSheet),
                    retailer = jpo.retailer?.let { User.fromJpo(it) },
                    wholesaler = jpo.wholesaler?.let { User.fromJpo(it) },
                    flower = Flower.fromJpo(jpo.flower),
                    quantity = Quantity.from(jpo.quantity),
                    grade = FlowerGrade.valueOf(jpo.grade.name),
                    price = jpo.price,
                    memo = jpo.memo,
                    createdAt = jpo.createdAt,
                    updatedAt = jpo.updatedAt,
                    deletedAt = jpo.deletedAt
            )
        }

        fun toJpo(domain: OrderItem): OrderItemJpo {
            return OrderItemJpo(
                    id = domain.id?.value,
                    orderSheet = OrderSheet.toJpo(domain.orderSheet),
                    retailer = domain.retailer?.let { User.toJpo(it) },
                    wholesaler = domain.wholesaler?.let { User.toJpo(it) },
                    flower = Flower.toJpo(domain.flower),
                    quantity = domain.quantity.value,
                    grade =
                            kr.co.marketbill.marketbillcoreserver.types.FlowerGrade.valueOf(
                                    domain.grade.name
                            ),
                    price = domain.price,
                    memo = domain.memo
            )
        }
    }
}
