package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.dto.GroupedCartItemCountDto
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepositoryCustom {
    fun countTotalPaginatedCartItemsBySessionIds(sessionIds : List<Long>) : List<GroupedCartItemCountDto>
}