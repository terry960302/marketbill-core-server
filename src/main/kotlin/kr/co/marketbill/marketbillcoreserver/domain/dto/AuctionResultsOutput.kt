package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import org.springframework.data.domain.Page

data class AuctionResultsOutput(
    val items: Page<AuctionResult>
)
