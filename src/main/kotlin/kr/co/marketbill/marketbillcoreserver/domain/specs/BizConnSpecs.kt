package kr.co.marketbill.marketbillcoreserver.domain.specs

import javax.persistence.criteria.Join
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class BizConnSpecs {

    companion object {

        fun byRetailerIds(userIds: List<Long>?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (userIds == null || userIds.isEmpty()) {
                    builder.conjunction()
                } else {
                    val retailer: Join<BizConnection, User> = root.join("retailer")
                    retailer.get<Long>("id").`in`(userIds)
                }
            }
        }

        fun byWholesalerIds(userIds: List<Long>?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (userIds == null || userIds.isEmpty()) {
                    builder.conjunction()
                } else {
                    val wholesaler: Join<BizConnection, User> = root.join("wholesaler")
                    wholesaler.get<Long>("id").`in`(userIds)
                }
            }
        }

        fun isApplyStatus(status: ApplyStatus?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (status == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<ApplyStatus>("applyStatus"), status)
                }
            }
        }

        fun hasApplyStatus(statuses: List<ApplyStatus>?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (statuses == null || statuses.isEmpty()) {
                    builder.conjunction()
                } else {
                    root.get<ApplyStatus>("applyStatus").`in`(statuses)
                }
            }
        }

        fun isRetailerId(retailerId: Long?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (retailerId == null) {
                    builder.conjunction()
                } else {
                    val retailer: Join<BizConnection, User> = root.join("retailer")
                    builder.equal(retailer.get<Long>("id"), retailerId)
                }
            }
        }

        fun isWholesalerId(wholesalerId: Long?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (wholesalerId == null) {
                    builder.conjunction()
                } else {
                    val wholesaler: Join<BizConnection, User> = root.join("wholesaler")
                    builder.equal(wholesaler.get<Long>("id"), wholesalerId)
                }
            }
        }
    }
}
