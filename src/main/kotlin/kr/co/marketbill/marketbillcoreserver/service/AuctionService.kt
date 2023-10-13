package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.AuctionResultRepository
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.IntStream

@Service
class AuctionService {
    @Autowired
    private lateinit var auctionResultRepository: AuctionResultRepository

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

            val auctionResult = auctionResultRepository.findAllByWholesalerIdAndAuctionDateIn(
                wholesalerId,
                dateList,
                pageable
            )
            logger.info("$className.$executedFunc >> completed.")
            return auctionResult
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }

    }
}
