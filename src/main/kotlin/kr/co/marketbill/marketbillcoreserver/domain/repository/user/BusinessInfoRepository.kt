package kr.co.marketbill.marketbillcoreserver.domain.repository.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BusinessInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface BusinessInfoRepository : JpaRepository<BusinessInfo, Long>, JpaSpecificationExecutor<BusinessInfo> {
    fun findByUserId(userId: Long): Optional<BusinessInfo>
}