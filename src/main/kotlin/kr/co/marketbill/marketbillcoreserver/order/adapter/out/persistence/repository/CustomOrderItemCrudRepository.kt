package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.CustomOrderItemJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CustomOrderItemCrudRepository :
        JpaRepository<CustomOrderItemJpo, Long>, JpaSpecificationExecutor<CustomOrderItemJpo> {
    fun findByOrderSheetId(orderSheetId: Long): List<CustomOrderItemJpo>
    fun deleteByIdIn(ids: List<Long>)
}
