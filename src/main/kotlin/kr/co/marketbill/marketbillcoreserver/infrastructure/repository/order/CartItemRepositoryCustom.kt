package kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order

import kr.co.marketbill.marketbillcoreserver.application.dto.request.GroupedCartItemCountDto
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepositoryCustom {
    fun countTotalPaginatedCartItemsBySessionIds(sessionIds : List<Long>) : List<GroupedCartItemCountDto>
}