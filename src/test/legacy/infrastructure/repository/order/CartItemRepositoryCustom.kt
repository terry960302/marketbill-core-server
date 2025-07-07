package kr.co.marketbill.marketbillcoreserver.legacy.infrastructure.repository.order

import kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.GroupedCartItemCountDto
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepositoryCustom {
    fun countTotalPaginatedCartItemsBySessionIds(sessionIds : List<Long>) : List<kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request.GroupedCartItemCountDto>
}