package kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity.ShoppingSessionJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShoppingSessionCrudRepository : JpaRepository<ShoppingSessionJpo, Long> {
    fun findByRetailerJpoAndDeletedAtIsNull(retailerId: Long): ShoppingSessionJpo?

    fun findByRetailerJpoInAndDeletedAtIsNull(retailerIds: List<Long>): List<ShoppingSessionJpo>
}
