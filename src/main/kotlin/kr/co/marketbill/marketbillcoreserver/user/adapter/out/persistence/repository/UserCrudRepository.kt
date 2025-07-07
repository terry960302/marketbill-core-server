package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCrudRepository : JpaRepository<UserJpo, Long> {
    fun findByUserCredentialJpo_PhoneNo(phoneNo : String) : UserJpo?
    fun existsByUserCredentialJpo_PhoneNo(phoneNo : String) : Boolean
}
