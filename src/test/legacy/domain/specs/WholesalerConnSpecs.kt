package kr.co.marketbill.marketbillcoreserver.legacy.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.WholesalerConnection
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class WholesalerConnSpecs {
    companion object {
        fun byEmployerId(userId: Long?): Specification<WholesalerConnection> {
            return Specification<WholesalerConnection> { root, query, builder ->
                if (userId == null) {
                    builder.conjunction()
                } else {
                    val employer = root.join<WholesalerConnection, User>("employer")
                    builder.equal(employer.get<Long>("id"), userId)
                }
            }
        }

        fun byEmployeeId(userId: Long?): Specification<WholesalerConnection> {
            return Specification<WholesalerConnection> { root, query, builder ->
                if (userId == null) {
                    builder.conjunction()
                } else {
                    val employee = root.join<WholesalerConnection, User>("employee")
                    builder.equal(employee.get<Long>("id"), userId)
                }
            }
        }
    }
}