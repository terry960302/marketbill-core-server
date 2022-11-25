package kr.co.marketbill.marketbillcoreserver.domain.repository.user

import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BizConnectionRepository : JpaRepository<BizConnection, Long>, JpaSpecificationExecutor<BizConnection> {
}