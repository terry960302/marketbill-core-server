package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import javax.persistence.criteria.Join

@Component
class BizConnSpecs {

    companion object {
        fun isRetailerId(retailerId: Long?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (retailerId == null) {
                    builder.conjunction()
                } else {
                    val retailer: Join<BizConnection, User> = root.join("retailer")
                    builder.equal(retailer.get<Long>("id"), retailerId)
                }
            };
        }

        fun isWholesalerId(wholesalerId: Long?): Specification<BizConnection> {
            return Specification<BizConnection> { root, query, builder ->
                if (wholesalerId == null) {
                    builder.conjunction()
                } else {
                    val wholesalerId: Join<BizConnection, User> = root.join("wholesalerId")
                    builder.equal(wholesalerId.get<Long>("id"), wholesalerId)
                }
            };
        }

    }
}