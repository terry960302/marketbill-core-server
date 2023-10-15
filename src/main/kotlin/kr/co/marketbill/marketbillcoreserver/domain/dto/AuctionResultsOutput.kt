package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResultWithGroupBy
import org.springframework.data.domain.Page

data class AuctionResultsOutput(
    val items: Page<AuctionResult>
)

data class AuctionResultDetailOutput(
    val item: AuctionResult
)

data class AuctionResultForSaleOutput(
    val wholesalerId: Long,
    val items: Page<AuctionResultWithGroupBy>
)

data class AuctionResultForSaleDetailOutput(
    val item: AuctionResult
)