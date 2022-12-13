package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
}