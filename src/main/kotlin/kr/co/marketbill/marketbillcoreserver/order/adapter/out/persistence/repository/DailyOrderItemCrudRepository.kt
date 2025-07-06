package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.DailyOrderItemJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface DailyOrderItemCrudRepository :
        JpaRepository<DailyOrderItemJpo, Long>, JpaSpecificationExecutor<DailyOrderItemJpo> {
    fun findByWholesalerId(wholesalerId: Long): List<DailyOrderItemJpo>
    fun findByFlowerId(flowerId: Long): List<DailyOrderItemJpo>
}
