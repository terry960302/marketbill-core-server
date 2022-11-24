package kr.co.marketbill.marketbillcoreserver.domain.repository.user

import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
    @Query("SELECT * FROM user_credentials AS uc WHERE uc.user_id = :userId AND uc.$SOFT_DELETE_CLAUSE", nativeQuery = true)
    fun getUserCredentialByUserId(userId: Long): Optional<UserCredential>

    @Query("SELECT * FROM user_credentials AS uc WHERE uc.phone_no = :phoneNo AND uc.$SOFT_DELETE_CLAUSE", nativeQuery = true)
    fun getUserCredentialByPhoneNo(phoneNo: String): Optional<UserCredential>
}