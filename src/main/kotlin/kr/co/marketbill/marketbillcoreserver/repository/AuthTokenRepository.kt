package kr.co.marketbill.marketbillcoreserver.repository

import kr.co.marketbill.marketbillcoreserver.entity.AuthToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthTokenRepository : JpaRepository<AuthToken, Long> {
}