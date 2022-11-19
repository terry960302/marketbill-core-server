package kr.co.marketbill.marketbillcoreserver.data.repository.flower

import kr.co.marketbill.marketbillcoreserver.data.entity.flower.FlowerType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FlowerTypeRepository : JpaRepository<FlowerType, Long>{

}