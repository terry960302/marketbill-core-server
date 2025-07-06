package kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerSearchCriteria
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult

interface FlowerRepository {
    fun findById(id: FlowerId): Flower?
    
    fun findFlowersWithCriteria(
        criteria: FlowerSearchCriteria,
        pageInfo: PageInfo
    ): PageResult<Flower>

    fun findBiddingFlowersByFlowerIds(ids: Set<FlowerId>): List<BiddingFlower>
}
