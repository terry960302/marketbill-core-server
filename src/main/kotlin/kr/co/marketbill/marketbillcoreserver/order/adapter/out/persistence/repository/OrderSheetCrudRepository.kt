package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.OrderSheetJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface OrderSheetCrudRepository :
        JpaRepository<OrderSheetJpo, Long>, JpaSpecificationExecutor<OrderSheetJpo> {
    fun findByOrderNo(orderNo: String): OrderSheetJpo?
}
