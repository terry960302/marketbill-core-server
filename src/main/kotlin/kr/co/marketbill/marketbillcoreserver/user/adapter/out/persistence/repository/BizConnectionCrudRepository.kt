package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.BizConnectionJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface BizConnectionCrudRepository :
        JpaRepository<BizConnectionJpo, Long>, JpaSpecificationExecutor<BizConnectionJpo> {}
