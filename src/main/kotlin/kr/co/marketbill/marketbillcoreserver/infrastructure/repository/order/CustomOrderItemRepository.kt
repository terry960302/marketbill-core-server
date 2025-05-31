package kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CustomOrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CustomOrderItemRepository : JpaRepository<CustomOrderItem, Long>, JpaSpecificationExecutor<CustomOrderItem> {
}