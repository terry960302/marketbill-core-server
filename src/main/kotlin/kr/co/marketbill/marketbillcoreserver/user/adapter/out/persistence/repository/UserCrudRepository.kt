package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface UserCrudRepository : JpaRepository<UserJpo, Long>, JpaSpecificationExecutor<UserJpo> {
    fun findByPhoneNo(phoneNo : String) : UserJpo?
    fun existsByPhoneNo(phoneNo : String) : Boolean
}
