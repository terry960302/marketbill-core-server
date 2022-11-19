package kr.co.marketbill.marketbillcoreserver.data.repository.user

import kr.co.marketbill.marketbillcoreserver.data.entity.user.AuthToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AuthTokenRepository : JpaRepository<AuthToken, Long> {
    fun findByUserId(userId : Long) : Optional<AuthToken>

    @Modifying
    override fun deleteById(id: Long)
}