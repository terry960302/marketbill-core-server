package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import java.util.Optional
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserCredentialJpo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserCredentialCrudRepository :
        JpaRepository<UserCredentialJpo, Long>, JpaSpecificationExecutor<UserCredentialJpo> {
//    @Query(
//            "SELECT * FROM user_credentials AS uc WHERE uc.user_id = :userId AND uc.$SOFT_DELETE_CLAUSE",
//            nativeQuery = true
//    )
//    fun getUserCredentialByUserId(userId: Long): Optional<UserCredentialJpo>
//
//    @Query(
//            "SELECT * FROM user_credentials AS uc WHERE uc.phone_no = :phoneNo AND uc.$SOFT_DELETE_CLAUSE",
//            nativeQuery = true
//    )
//    fun getUserCredentialByPhoneNo(phoneNo: String): Optional<UserCredentialJpo>
}
