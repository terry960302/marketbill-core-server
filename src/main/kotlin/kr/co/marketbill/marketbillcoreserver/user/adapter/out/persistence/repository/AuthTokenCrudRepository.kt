package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import java.util.Optional
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.AuthTokenJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthTokenCrudRepository : JpaRepository<AuthTokenJpo, Long> {
    fun findByUserId(userId: Long): Optional<AuthTokenJpo>
}
