package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class FlowerSpecs {
    companion object {
        fun nameLike(keyword: String?): Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                if (keyword == null) {
                    builder.conjunction()
                } else {
                    builder.like(root.get("name"), "%${keyword}%")
                }
            };
        }

        fun btwDates(fromDate: LocalDate?, toDate: LocalDate?): Specification<Flower> {
            return Specification<Flower> { root, query, builder ->
                if (fromDate == null || toDate == null) {
                    builder.conjunction()
                } else {
                    val biddingFlower = root.join<Flower, BiddingFlower>("biddingFlowers")
                    val biddingDate = biddingFlower.get<LocalDateTime>("biddingDate").`as`(LocalDate::class.java)
                    builder.between(biddingDate, fromDate, toDate)
                }
            };
        }
    }
}