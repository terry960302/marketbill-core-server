package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.data.entity.flower.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.data.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.data.entity.flower.FlowerType
import kr.co.marketbill.marketbillcoreserver.data.repository.flower.BiddingFlowerRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.flower.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.flower.FlowerTypeRepository
import kr.co.marketbill.marketbillcoreserver.data.specs.FlowerSpecs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

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