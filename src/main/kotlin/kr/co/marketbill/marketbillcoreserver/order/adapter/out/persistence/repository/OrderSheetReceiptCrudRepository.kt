package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.OrderSheetReceiptJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface OrderSheetReceiptCrudRepository :
        JpaRepository<OrderSheetReceiptJpo, Long>, JpaSpecificationExecutor<OrderSheetReceiptJpo> {
    fun findByOrderSheetId(orderSheetId: Long): List<OrderSheetReceiptJpo>
}
