package kr.co.marketbill.marketbillcoreserver.data.repository.order

import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.data.entity.order.OrderItem
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    @Query(
        "SELECT * FROM order_items AS oi WHERE oi.order_sheet_id IN :orderSheetIds AND oi.$SOFT_DELETE_CLAUSE",
        nativeQuery = true
    )
    fun getAllOrderItemsByOrderSheetIds(orderSheetIds: List<Long>, pageable: Pageable): List<OrderItem>
}