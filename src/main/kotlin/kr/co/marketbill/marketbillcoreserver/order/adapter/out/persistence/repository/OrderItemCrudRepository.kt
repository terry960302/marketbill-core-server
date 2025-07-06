package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.OrderItemJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface OrderItemCrudRepository :
        JpaRepository<OrderItemJpo, Long>, JpaSpecificationExecutor<OrderItemJpo> {
    fun findByOrderSheetId(orderSheetId: Long): List<OrderItemJpo>
}
