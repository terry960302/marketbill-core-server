package kr.co.marketbill.marketbillcoreserver.order.domain.model

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.DailyOrderItemJpo
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.DailyOrderItemId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User

data class DailyOrderItem(
        val id: DailyOrderItemId?,
        val wholesaler: User?,
        val flower: Flower,
        val grade: FlowerGrade,
        val price: Int?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    init {
        require(price == null || price >= 0) { "가격은 0 이상이어야 합니다" }
    }

    fun updatePrice(newPrice: Int): DailyOrderItem {
        require(newPrice >= 0) { "가격은 0 이상이어야 합니다" }
        return copy(price = newPrice)
    }

    companion object {
        fun fromJpo(jpo: DailyOrderItemJpo): DailyOrderItem {
            return DailyOrderItem(
                    id = jpo.id?.let { DailyOrderItemId.from(it) },
                    wholesaler = jpo.wholesaler?.let { User.fromJpo(it) },
                    flower = Flower.fromJpo(jpo.flower),
                    grade = FlowerGrade.valueOf(jpo.grade.name),
                    price = jpo.price,
                    createdAt = jpo.createdAt,
                    updatedAt = jpo.updatedAt,
                    deletedAt = jpo.deletedAt
            )
        }

        fun toJpo(domain: DailyOrderItem): DailyOrderItemJpo {
            return DailyOrderItemJpo(
                    id = domain.id?.value,
                    wholesaler = domain.wholesaler?.let { User.toJpo(it) },
                    flower = Flower.toJpo(domain.flower),
                    grade =
                            kr.co.marketbill.marketbillcoreserver.types.FlowerGrade.valueOf(
                                    domain.grade.name
                            ),
                    price = domain.price
            )
        }
    }
}
