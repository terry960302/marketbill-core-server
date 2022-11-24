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
import java.util.*

@Service
class FlowerService {
    @Autowired
    private lateinit var flowerRepository: FlowerRepository


    val logger: Logger = LoggerFactory.getLogger(FlowerService::class.java)

    fun getAllBuyableFlowers(pageable: Pageable): Page<Flower> {
        return flowerRepository.getAllBuyableFlowers(Date(), pageable)
    }

    fun searchFlowers(keyword: String, pageable: Pageable): Page<Flower> {
        return flowerRepository.findAll(FlowerSpecs.nameLike(keyword), pageable)
    }
    fun getSearchFlowersTotalCount(keyword :String): Int{
        return flowerRepository.getSearchFlowersCount(keyword).toInt()
    }


}