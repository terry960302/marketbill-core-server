package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class OrderSheetSpecs {
    companion object {
        fun byUserId(userId: Long?, role: AccountRole?): Specification<OrderSheet> {
            return Specification<OrderSheet> { root, query, builder ->
                if (userId == null || role == null) {
                    builder.conjunction()
                } else {
                    if (role == AccountRole.RETAILER) {
                        val retailer = root.join<OrderSheet, User>("retailer")
                        builder.equal(retailer.get<Long>("id"), userId)
                    } else {
                        val wholesaler = root.join<OrderSheet, User>("wholesaler")
                        builder.equal(wholesaler.get<Long>("id"), userId)
                    }
                }

            }
        }

        fun atDate(date: LocalDate?): Specification<OrderSheet> {
            return Specification<OrderSheet> { root, query, builder ->
                if (date == null) {
                    builder.conjunction()
                } else {
                    val createdAt = root.get<LocalDateTime>("createdAt").`as`(LocalDate::class.java)
                    builder.equal(createdAt, date)
                }
            }
        }
    }
}