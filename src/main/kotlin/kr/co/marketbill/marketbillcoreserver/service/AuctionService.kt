package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.AuctionResultRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.AuctionResultSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.FlowerSpecs
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.IntStream

@Service
class AuctionService {
    @Autowired
    private lateinit var auctionResultRepository: AuctionResultRepository

    @Autowired
    private lateinit var flowerRepository: FlowerRepository

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(AuctionService::class.java)
    private val className: String = this.javaClass.simpleName

    private val DEFAULT_BEFORE_DAYS = 3

    @Transactional(readOnly = true)
    fun getAuctionResult(
        auctionDate: LocalDate,
        beforeDays: Int?,
        wholesalerId: Long,
        pageable: Pageable
    ): Page<AuctionResult> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val dateList = IntStream.rangeClosed(0, beforeDays ?: DEFAULT_BEFORE_DAYS)
                .boxed()
                .map {
                    val previousDate = auctionDate.minusDays(it.toLong())
                    previousDate.format(formatter).toInt()
                }.toList()

            val auctionResult = auctionResultRepository.findAll(
                AuctionResultSpecs.byWholesalerId(wholesalerId)
                    .and(AuctionResultSpecs.byAuctionDates(dateList)),
                pageable
            ).map {
                val flower = flowerRepository.findAll(
                    FlowerSpecs.nameAndTypeNameLike(it.flowerName, it.flowerTypeName)
                )

                AuctionResult(
                    id = it.id,
                    flowerName = it.flowerName,
                    flowerTypeName = it.flowerTypeName,
                    flowerGrade = it.flowerGrade,
                    boxCount = it.boxCount,
                    flowerCount = it.flowerCount,
                    price = it.price,
                    totalPrice = it.totalPrice,
                    serialCode = it.serialCode,
                    wholesalerId = it.wholesalerId,
                    auctionDate = it.auctionDate,
                    images = flower.firstOrNull()?.images ?: emptyList(),
                    retailPrice = it.retailPrice,
                    isSoldOut = it.isSoldOut
                )
            }

            logger.info("$className.$executedFunc >> completed.")
            return auctionResult
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getAuctionResultDetail(id: Long): AuctionResult {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val auctionResult = auctionResultRepository.findById(id).orElseThrow { Exception("Not Found") }
            val flower = flowerRepository.findAll(
                FlowerSpecs.nameAndTypeNameLike(auctionResult.flowerName, auctionResult.flowerTypeName)
            )

            logger.info("$className.$executedFunc >> completed.")
            return AuctionResult(
                id = auctionResult.id,
                flowerName = auctionResult.flowerName,
                flowerTypeName = auctionResult.flowerTypeName,
                flowerGrade = auctionResult.flowerGrade,
                boxCount = auctionResult.boxCount,
                flowerCount = auctionResult.flowerCount,
                price = auctionResult.price,
                totalPrice = auctionResult.totalPrice,
                serialCode = auctionResult.serialCode,
                wholesalerId = auctionResult.wholesalerId,
                auctionDate = auctionResult.auctionDate,
                images = flower.firstOrNull()?.images ?: emptyList(),
                retailPrice = auctionResult.retailPrice,
                isSoldOut = auctionResult.isSoldOut
            )
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }
}
