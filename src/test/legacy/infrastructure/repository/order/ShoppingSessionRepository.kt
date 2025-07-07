package kr.co.marketbill.marketbillcoreserver.legacy.infrastructure.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ShoppingSessionRepository : JpaRepository<ShoppingSession, Long>, JpaSpecificationExecutor<ShoppingSession> {
    fun findAllByRetailerId(retailerId : Long, pageable: Pageable) : Page<ShoppingSession>
}