package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResultWithGroupBy
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.AuctionResultRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.AuctionResultSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.FlowerSpecs
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
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

                it.copy(
                    images = flower.firstOrNull()?.images ?: emptyList(),
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
            return auctionResult.copy(
                images = flower.firstOrNull()?.images ?: emptyList(),
            )
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun updateAuctionResult(id: Long, retailPrice: Int?, isSoldOut: Boolean?): AuctionResult {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val oldAuctionResult = auctionResultRepository.findById(id).orElseThrow { Exception("Not Found") }
            val newAuctionResult = oldAuctionResult.copy(
                retailPrice = retailPrice ?: oldAuctionResult.retailPrice,
                isSoldOut = isSoldOut ?: oldAuctionResult.isSoldOut,
            )

            return auctionResultRepository.save(newAuctionResult)
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getAuctionResultForSale(wholesalerId: Long, pageable: Pageable): Page<AuctionResultWithGroupBy> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val auctionDates = IntStream.rangeClosed(0, DEFAULT_BEFORE_DAYS)
                .boxed()
                .map {
                    val previousDate = currentDate.minusDays(it.toLong())
                    previousDate.format(formatter).toInt()
                }.toList()

            val auctionResultWithGroupBy =
                auctionResultRepository.findGroupByFlowerNameAndAuctionDate(wholesalerId, auctionDates, pageable)
                    .map {
                        val flower = flowerRepository.findAll(
                            FlowerSpecs.nameAndTypeNameLike(it.flowerName, it.flowerTypeName)
                        )

                        AuctionResultWithGroupBy.of(it, flower.firstOrNull()?.images ?: emptyList())
                    }

            logger.info("$className.$executedFunc >> completed.")
            val pageRequest = PageRequest.of(pageable.pageNumber, pageable.pageSize)
            val start = pageRequest.offset
            val end = (start + pageRequest.pageSize).coerceAtMost(auctionResultWithGroupBy.size.toLong()).toInt()
            return PageImpl(
                auctionResultWithGroupBy.subList(start.toInt(), end),
                pageRequest,
                auctionResultWithGroupBy.size.toLong()
            )
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getAuctionResultForSaleDetail(id: Long): AuctionResult {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val auctionResult = auctionResultRepository.findById(id).orElseThrow { Exception("Not Found") }
            val flower = flowerRepository.findAll(
                FlowerSpecs.nameAndTypeNameLike(auctionResult.flowerName, auctionResult.flowerTypeName)
            )

            logger.info("$className.$executedFunc >> completed.")
            return auctionResult.copy(
                images = flower.firstOrNull()?.images ?: emptyList(),
            )
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }
}
