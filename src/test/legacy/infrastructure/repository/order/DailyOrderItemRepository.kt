package kr.co.marketbill.marketbillcoreserver.legacy.infrastructure.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.DailyOrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface DailyOrderItemRepository : JpaRepository<DailyOrderItem, Long>, JpaSpecificationExecutor<DailyOrderItem> {
}