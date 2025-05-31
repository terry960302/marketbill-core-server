package kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.user.WholesalerConnection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface WholesalerConnectionRepository : JpaRepository<WholesalerConnection, Long>, JpaSpecificationExecutor<WholesalerConnection>{
}