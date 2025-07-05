package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.WholesalerConnectionJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface WholesalerConnectionCrudRepository :
        JpaRepository<WholesalerConnectionJpo, Long>,
        JpaSpecificationExecutor<WholesalerConnectionJpo> {}
