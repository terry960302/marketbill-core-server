package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.domain.entity.user.AuthToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AuthTokenRepository : JpaRepository<AuthToken, Long> {
    fun findByUserId(userId : Long) : Optional<AuthToken>
}