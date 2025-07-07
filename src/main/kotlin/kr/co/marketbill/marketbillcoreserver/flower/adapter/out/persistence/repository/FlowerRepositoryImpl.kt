package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.mapper.BiddingFlowerMapper
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.mapper.FlowerMapper
import kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerSearchCriteria
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.stereotype.Repository

@Repository
class FlowerRepositoryImpl(
    private val flowerQueryRepository: FlowerQueryRepository,
    private val flowerMapper: FlowerMapper,
    private val crudRepository: FlowerCrudRepository,
    private val biddingFlowerCrudRepository: BiddingFlowerCrudRepository,
) : FlowerRepository {
    override fun findById(id: FlowerId): Flower? {
        val jpo = crudRepository.findById(id.value).orElseThrow() ?: throw MarketbillException(ErrorCode.NO_FLOWER)
        return Flower.fromJpo(jpo)

    }

    override fun findFlowersWithCriteria(
        criteria: FlowerSearchCriteria,
        pageInfo: PageInfo
    ): PageResult<Flower> {

        val jpoPage = flowerQueryRepository.findFlowersWithDynamicQuery(criteria, pageInfo)

        val domainFlowers = flowerMapper.toDomainList(jpoPage.content)

        return PageResult(
            content = domainFlowers,
            pageInfo = pageInfo,
            totalElements = jpoPage.totalElements
        )
    }

    override fun findBiddingFlowersByFlowerIds(ids: Set<FlowerId>): List<BiddingFlower> {
        return biddingFlowerCrudRepository.findAllByFlowerIdIn(ids.map { it.value }.toSet())
            .map { BiddingFlower.fromJpo(it) };
    }
}
