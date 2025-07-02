package kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerSearchCriteria
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult

interface FlowerRepository {
    fun findFlowersWithCriteria(
        criteria: FlowerSearchCriteria,
        pageInfo: PageInfo
    ): PageResult<Flower>
}
