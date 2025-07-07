package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import java.util.Optional
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserCredentialJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserCredentialCrudRepository :
        JpaRepository<UserCredentialJpo, Long> {
}
