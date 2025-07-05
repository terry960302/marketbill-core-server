package kr.co.marketbill.marketbillcoreserver.legacy.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.vo.DailyOrderItemKey
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

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

        fun byUserId(userId: Long?, role: AccountRole?): Specification<OrderItem> {
            return Specification<OrderItem> { root, query, builder ->
                if (userId == null || role == null) {
                    builder.conjunction()
                } else {
                    if (role == AccountRole.RETAILER) {
                        val retailer = root.join<OrderItem, User>("retailer")
                        builder.equal(retailer.get<Long>("id"), userId)
                    } else {
                        val wholesaler = root.join<OrderItem, User>("wholesaler")
                        builder.equal(wholesaler.get<Long>("id"), userId)
                    }
                }

            }
        }

        fun excludeId(orderItemId: Long?): Specification<OrderItem> {
            return Specification<OrderItem> { root, query, builder ->
                if (orderItemId == null) {
                    builder.conjunction()
                } else {
                    builder.notEqual(root.get<Long>("id"), orderItemId)
                }

            }
        }

        fun atDate(date: LocalDate?): Specification<OrderItem> {
            return Specification<OrderItem> { root, query, builder ->
                if (date == null) {
                    builder.conjunction()
                } else {
                    val createdAt = root.get<LocalDateTime>("createdAt").`as`(LocalDate::class.java)
                    builder.equal(createdAt, date)
                }

            }
        }

        fun byItemKey(key: DailyOrderItemKey?): Specification<OrderItem> {
            return Specification<OrderItem> { root, query, builder ->
                if (key == null) {
                    builder.conjunction()
                } else {
                    val wholesaler = root.join<OrderItem, User>("wholesaler")
                    val flower = root.join<OrderItem, Flower>("flower")

                    val wholesalerPredicate = builder.equal(wholesaler.get<Long>("id"), key.wholesalerId)
                    val flowerPredicate = builder.equal(flower.get<Long>("id"), key.flowerId)
                    val gradePredicate = builder.equal(root.get<String>("grade"), key.grade)
                    val createdAt = root.get<LocalDateTime>("createdAt").`as`(LocalDate::class.java)
                    val datePredicate = builder.equal(createdAt, key.date)
                    builder.and(datePredicate, wholesalerPredicate, flowerPredicate, gradePredicate)
                }

            }
        }

    }
}