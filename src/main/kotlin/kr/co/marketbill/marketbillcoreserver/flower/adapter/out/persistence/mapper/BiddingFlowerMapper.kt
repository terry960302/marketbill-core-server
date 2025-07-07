package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.mapper

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.BiddingFlowerJpo
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.mapper.DomainPersistenceMapper
import org.springframework.stereotype.Component

@Component
class BiddingFlowerMapper(private val flowerMapper: FlowerMapper) : DomainPersistenceMapper<BiddingFlower, BiddingFlowerJpo>() {

    fun toDomainList(jpos: List<BiddingFlowerJpo>): List<BiddingFlower> {
        return jpos.map { toDomain(it) }
    }


    override fun toDomain(jpo: BiddingFlowerJpo): BiddingFlower {
        return BiddingFlower(
            id = jpo.id?.let { BiddingFlowerId(it) },
            flower = jpo.flower.let { flowerMapper.toDomain(jpo.flower) },
            biddingDate = jpo.biddingDate
        )
    }

    override fun toJpo(domain: BiddingFlower): BiddingFlowerJpo {
//        return BiddingFlowerJpo.create(
//            domain.flower.let { Flower.fromJpo(domain.flower) },
//            biddingDate = domain.biddingDate
//        )
        TODO("do nothing")
    }
}
