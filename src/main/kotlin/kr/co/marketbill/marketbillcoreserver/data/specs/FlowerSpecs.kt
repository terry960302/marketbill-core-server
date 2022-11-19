package kr.co.marketbill.marketbillcoreserver.data.specs

import kr.co.marketbill.marketbillcoreserver.data.entity.flower.Flower
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class FlowerSpecs{
    companion object{
        fun nameLike(keyword : String) : Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                builder.like(root.get("name"), "%${keyword}%")
            };
        }
    }
}