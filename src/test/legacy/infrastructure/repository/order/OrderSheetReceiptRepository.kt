package kr.co.marketbill.marketbillcoreserver.legacy.infrastructure.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface OrderSheetReceiptRepository : JpaRepository<OrderSheetReceipt, Long>, JpaSpecificationExecutor<OrderSheetReceipt>{
}