package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface FlowerCrudRepository : JpaRepository<FlowerJpo, Long>, JpaSpecificationExecutor<FlowerJpo> {

}