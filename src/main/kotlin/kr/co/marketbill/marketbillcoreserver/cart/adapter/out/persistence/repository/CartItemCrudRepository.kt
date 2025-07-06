package kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity.CartItemJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartItemCrudRepository : JpaRepository<CartItemJpo, Long> {
    fun findByRetailerJpoAndDeletedAtIsNull(retailerId: Long): List<CartItemJpo>

    fun findByRetailerJpoAndWholesalerJpoAndDeletedAtIsNull(
            retailerId: Long,
            wholesalerId: Long
    ): List<CartItemJpo>
}
