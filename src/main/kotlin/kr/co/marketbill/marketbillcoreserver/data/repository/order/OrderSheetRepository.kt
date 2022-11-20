package kr.co.marketbill.marketbillcoreserver.data.repository.order

import kr.co.marketbill.marketbillcoreserver.data.entity.order.OrderSheet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderSheetRepository : JpaRepository<OrderSheet, Long> {
    fun findAllByRetailerId(retailerId: Long, pageable: Pageable): Page<OrderSheet>
    fun findAllByWholesalerId(wholesalerId: Long, pageable: Pageable): Page<OrderSheet>

}