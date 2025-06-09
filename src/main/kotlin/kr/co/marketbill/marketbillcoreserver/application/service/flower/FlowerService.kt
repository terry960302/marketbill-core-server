package kr.co.marketbill.marketbillcoreserver.application.service.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.specs.FlowerSpecs
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.flower.FlowerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class FlowerService {
    @Autowired
    private lateinit var flowerRepository: FlowerRepository


    @Transactional(readOnly = true)
    fun getFlowers(fromDate: LocalDate?, toDate: LocalDate?, keyword: String?, pageable: Pageable): Page<Flower> {
        val flowers = flowerRepository.findAll(
            FlowerSpecs.btwDates(fromDate, toDate).and(FlowerSpecs.nameLike(keyword)),
            pageable
        )
        return flowers
    }

}