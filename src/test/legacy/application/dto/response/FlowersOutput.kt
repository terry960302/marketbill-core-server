package kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response

import kr.co.marketbill.marketbillcoreserver.types.Flower
import org.springframework.data.domain.Page

data class FlowersOutput(
    val resultCount: Long,
    val items: Page<Flower>
)
