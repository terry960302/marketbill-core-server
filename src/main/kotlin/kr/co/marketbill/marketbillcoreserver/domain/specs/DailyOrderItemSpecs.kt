package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.DailyOrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.vo.DailyOrderItemKey
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DailyOrderItemSpecs {
    companion object {
        fun byItemKey(key: DailyOrderItemKey?): Specification<DailyOrderItem> {
            return Specification<DailyOrderItem> { root, query, builder ->
                if (key == null) {
                    builder.conjunction()
                } else {
                    val wholesaler = root.join<DailyOrderItem, User>("wholesaler")
                    val flower = root.join<DailyOrderItem, Flower>("flower")

                    val wholesalerPredicate = builder.equal(wholesaler.get<Long>("id"), key.wholesalerId)
                    val flowerPredicate = builder.equal(flower.get<Long>("id"), key.flowerId)
                    val gradePredicate = builder.equal(root.get<String>("grade"), key.grade)
                    val createdAt = root.get<LocalDateTime>("createdAt").`as`(LocalDate::class.java)
                    val datePredicate = builder.equal(createdAt, key.date)
                    builder.and(datePredicate, wholesalerPredicate, flowerPredicate, gradePredicate)
                }
            }
        }

        fun btwDates(fromDate: LocalDate?, toDate: LocalDate?): Specification<DailyOrderItem> {
            return Specification<DailyOrderItem> { root, query, builder ->
                if (fromDate == null || toDate == null) {
                    builder.conjunction()
                } else {
                    val createdDate = root.get<LocalDateTime>("createdAt").`as`(LocalDate::class.java)
                    builder.between(createdDate, fromDate, toDate)
                }
            }
        }

        fun byWholesalerId(wholesalerId: Long?): Specification<DailyOrderItem> {
            return Specification<DailyOrderItem> { root, query, builder ->
                if (wholesalerId == null) {
                    builder.conjunction()
                } else {
                    val wholesaler = root.join<DailyOrderItem, User>("wholesaler")
                    builder.equal(wholesaler.get<Long>("id"), wholesalerId)
                }
            }
        }
    }
}