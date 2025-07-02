package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.mapper.FlowerMapper
import kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerSearchCriteria
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import org.springframework.stereotype.Repository

@Repository
class FlowerRepositoryImpl(
    private val flowerQueryRepository: FlowerQueryRepository,
    private val flowerMapper: FlowerMapper,
    private val crudRepository: FlowerCrudRepository,
) : FlowerRepository {

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
}
