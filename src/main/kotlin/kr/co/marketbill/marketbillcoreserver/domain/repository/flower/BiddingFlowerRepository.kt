package kr.co.marketbill.marketbillcoreserver.domain.repository.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.BiddingFlower
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BiddingFlowerRepository : JpaRepository<BiddingFlower, Long>{
}