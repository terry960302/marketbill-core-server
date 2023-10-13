package kr.co.marketbill.marketbillcoreserver.domain.repository.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuctionResultRepository: JpaRepository<AuctionResult, Long> {
    fun findAllByWholesalerIdAndAuctionDate(wholesalerId: Long, auctionDate: Int,pageable: Pageable): Page<AuctionResult>
    fun findAllByWholesalerIdAndAuctionDateIn(wholesalerId: Long, auctionDate: List<Int>, pageable: Pageable): Page<AuctionResult>
}