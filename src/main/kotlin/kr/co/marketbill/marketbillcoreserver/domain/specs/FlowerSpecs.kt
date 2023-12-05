package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.FlowerType
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class FlowerSpecs {
    companion object {
        fun createdAtDesc(): Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                query.orderBy(builder.desc(root.get<LocalDateTime>("createdAt")))
                null
            }
        }

        fun nameLike(keyword: String?): Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                if (keyword == null) {
                    builder.conjunction()
                } else {
                    val namePredicate = builder.like(root.get("name"), "%${keyword}%")
                    val flowerType = root.join<Flower, FlowerType>("flowerType")
                    val typeNamePredicate = builder.like(flowerType.get("name"), "%${keyword}%")
                    builder.or(namePredicate, typeNamePredicate)
                }
            }
        }

        fun nameAndTypeNameLike(name: String?, typeName: String?): Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                val namePredicate = builder.like(root.get("name"), "%${name}%")
                val flowerType = root.join<Flower, FlowerType>("flowerType")
                val typeNamePredicate = builder.like(flowerType.get("name"), "%${typeName}%")
                builder.and(namePredicate, typeNamePredicate)
            }
        }

        fun btwDates(fromDate: LocalDate?, toDate: LocalDate?): Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                if (fromDate == null || toDate == null) {
                    builder.conjunction()
                } else {
                    query.distinct(true)
                    val biddingFlower = root.join<Flower, BiddingFlower>("biddingFlowers")
                    val biddingDate = biddingFlower.get<LocalDateTime>("biddingDate").`as`(LocalDate::class.java)
                    builder.between(biddingDate, fromDate, toDate)
                }
            }
        }

        fun imagesDesc(): Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                query.orderBy(builder.desc(root.get<String>("images")))
                builder.conjunction()
            }
        }
    }
}