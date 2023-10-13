package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class AuctionResultSpecs {
    companion object {
        fun byWholesalerId(wholesalerId: Long?): Specification<AuctionResult> {
            return Specification<AuctionResult> { root, query, builder ->
                if (wholesalerId == null) {
                    builder.conjunction()
                } else {
                    val wholesaler = root.join<AuctionResult, kr.co.marketbill.marketbillcoreserver.domain.entity.user.User>("wholesaler")
                    builder.equal(wholesaler.get<Long>("id"), wholesalerId)
                }
            }
        }
    }
}