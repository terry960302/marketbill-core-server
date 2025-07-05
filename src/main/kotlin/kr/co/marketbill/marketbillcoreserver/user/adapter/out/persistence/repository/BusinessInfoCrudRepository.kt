package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import java.util.Optional
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.BusinessInfoJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface BusinessInfoCrudRepository :
        JpaRepository<BusinessInfoJpo, Long>, JpaSpecificationExecutor<BusinessInfoJpo> {
    fun findByUserId(userId: Long): Optional<BusinessInfoJpo>
}
