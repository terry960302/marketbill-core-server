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
    private lateinit var entityManager: EntityManager
    @Autowired
    private lateinit var flowerRepository: FlowerRepository
    @Autowired
    private lateinit var flowerTypeRepository: FlowerTypeRepository
    @Autowired
    private lateinit var biddingFlowerRepository: BiddingFlowerRepository

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

    @PostConstruct
    fun createMockFlowers() {
        val flowerType1 = FlowerType(1, "국화")
        val flowerType2 = FlowerType(2, "과꽃")
        flowerTypeRepository.saveAll(mutableListOf(flowerType1, flowerType2))

        val flowerNames1 = arrayListOf<String>("테데오옐로우", "신명", "상그릴라", "설악", "백장미소국", "이소국(옐로우)", "대국", "필링다크")
        val flowerNames2 = arrayListOf<String>(
            "코코토(미니)",
            "씨스타화이트",
            "코코토화이트(미니)",
            "레지스트",
            "아즈미화이트",
            "라이트블루",
            "코코토핑크(미니)",
            "송본화이트",
            "미니과꽃(로즈화이트)",
            "옐로우"
        )

        val flowers1 = flowerNames1.map { name ->
            Flower(flowerType = entityManager.getReference(FlowerType::class.java, 1.toLong()), name = name)
        }
        val flowers2 = flowerNames2.map { name ->
            Flower(flowerType = entityManager.getReference(FlowerType::class.java, 2.toLong()), name = name)
        }
        val flowers = flowerRepository.saveAll(flowers1 + flowers2)


        val biddingFlowers = flowers.map {
            BiddingFlower(flower = it, biddingDate = LocalDateTime.now())
        }
        biddingFlowerRepository.saveAll(biddingFlowers)

        logger.trace("createMockFlowers completed")
    }
}