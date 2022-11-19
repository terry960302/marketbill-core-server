package kr.co.marketbill.marketbillcoreserver.data.repository.order

import kr.co.marketbill.marketbillcoreserver.data.entity.order.CartItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<CartItem, Long> {
    @Query(
        "SELECT * FROM cart_items AS ci INNER JOIN users AS u ON u.id = ci.retailer_id WHERE ci.retailer_id = :userId",
        nativeQuery = true
    )
    fun getAllCartItems(userId: Long, pageable: Pageable): Page<CartItem>
}