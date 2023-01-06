package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.FlowerSpecs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class FlowerService {
    @Autowired
    private lateinit var flowerRepository: FlowerRepository

    private val logger: Logger = LoggerFactory.getLogger(FlowerService::class.java)
    private val className: String = this.javaClass.simpleName

    fun getFlowers(fromDate: LocalDate?, toDate: LocalDate?, keyword: String?, pageable: Pageable): Page<Flower> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val flowers = flowerRepository.findAll(
                FlowerSpecs.btwDates(fromDate, toDate).and(FlowerSpecs.nameLike(keyword)),
                pageable
            )
            logger.info("$className.$executedFunc >> completed.")
            return flowers
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

}