package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import org.springframework.data.domain.Page

data class AuctionResultsOutput(
    val items: Page<AuctionResult>
)

data class AuctionResultDetailOutput(
    val item: AuctionResult
)