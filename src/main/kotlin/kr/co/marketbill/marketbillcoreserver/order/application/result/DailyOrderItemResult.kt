package kr.co.marketbill.marketbillcoreserver.order.application.result

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.domain.model.DailyOrderItem
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerResult

data class DailyOrderItemResult(
        val id: Long,
        val wholesaler: UserResult?,
        val flower: FlowerResult,
        val grade: String,
        val price: Int?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val deletedAt: LocalDateTime?
) {
    companion object {
        fun from(dailyOrderItem: DailyOrderItem): DailyOrderItemResult {
            return DailyOrderItemResult(
                    id = dailyOrderItem.id?.value ?: 0L,
                    wholesaler = dailyOrderItem.wholesaler?.let { UserResult.from(it) },
                    flower = FlowerResult.from(dailyOrderItem.flower),
                    grade = dailyOrderItem.grade.name,
                    price = dailyOrderItem.price,
                    createdAt = dailyOrderItem.createdAt,
                    updatedAt = dailyOrderItem.updatedAt,
                    deletedAt = dailyOrderItem.deletedAt
            )
        }
    }
}
