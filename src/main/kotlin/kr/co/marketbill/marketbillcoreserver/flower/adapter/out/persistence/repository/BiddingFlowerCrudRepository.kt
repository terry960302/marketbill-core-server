package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.BiddingFlowerJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BiddingFlowerCrudRepository : JpaRepository<BiddingFlowerJpo, Long>{
}