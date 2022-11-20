package kr.co.marketbill.marketbillcoreserver.data.repository.order

import kr.co.marketbill.marketbillcoreserver.data.entity.order.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long>{
}