package kr.co.marketbill.marketbillcoreserver.infrastructure.repository.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface FlowerRepository : JpaRepository<Flower, Long>, JpaSpecificationExecutor<Flower> {
}