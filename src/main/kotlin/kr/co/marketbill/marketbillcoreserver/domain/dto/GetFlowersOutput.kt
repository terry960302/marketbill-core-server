package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import org.springframework.data.domain.Page

data class GetFlowersOutput(var resultCount: Long, var flowers: Page<Flower>)
