package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class AuctionResultSpecs {
    companion object {
        fun byWholesalerId(wholesalerId: Long): Specification<AuctionResult> {
            return Specification<AuctionResult> { root, query, builder ->
                builder.equal(root.get<Long>("wholesalerId"), wholesalerId)
            }
        }

        fun byAuctionDates(auctionDates: List<Int>): Specification<AuctionResult> {
            return Specification<AuctionResult> { root, query, builder ->
                root.get<Int>("auctionDate").`in`(auctionDates)
            }
        }
    }
}