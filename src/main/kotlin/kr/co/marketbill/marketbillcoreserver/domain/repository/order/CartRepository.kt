package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<CartItem, Long> {
    fun findAllByRetailerId(retailerId : Long, pageable: Pageable) : Page<CartItem>
}