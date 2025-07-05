package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.mapper

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerColorJpo
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerJpo
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerTypeJpo
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerColor
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerType
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.mapper.DomainPersistenceMapper
import org.springframework.stereotype.Component

@Component
class FlowerMapper : DomainPersistenceMapper<Flower, FlowerJpo>() {

    override fun toDomain(jpo: FlowerJpo): Flower {
        return Flower(
            id = jpo.id?.let { FlowerId(it) },
            name = jpo.name,
            type = FlowerType(
                id = jpo.flowerTypeJpo.id?.let { FlowerTypeId(it) },
                name = jpo.flowerTypeJpo.name,
                imgUrl = jpo.flowerTypeJpo.imgUrl
            ),
            images = jpo.images.map { FlowerImageUrl.from(it) },
            color = FlowerColor.fromJpo(jpo.flowerColor),
            createdAt = jpo.createdAt,
            updatedAt = jpo.updatedAt
        )
    }

    fun toDomainList(jpos: List<FlowerJpo>): List<Flower> {
        return jpos.map { toDomain(it) }
    }

    override fun toJpo(domain: Flower): FlowerJpo {
        return FlowerJpo.create(
            name = domain.name,
            type = FlowerTypeJpo.create(domain.type.name, domain.type.imgUrl),
            color = domain.color.let { FlowerColorJpo.create(domain.color.name) },
            images = domain.images.map { it.value },
        )
    }
}
