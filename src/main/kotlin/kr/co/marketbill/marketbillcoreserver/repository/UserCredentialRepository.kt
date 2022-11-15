package kr.co.marketbill.marketbillcoreserver.repository

import kr.co.marketbill.marketbillcoreserver.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
    @Query("SELECT * FROM user_credentials AS uc WHERE uc.user_id = :userId", nativeQuery = true)
    fun getUserCredentialByUserId(userId: Long): Optional<UserCredential>

    @Query("SELECT * FROM user_credentials AS uc WHERE uc.phone_no = :phoneNo", nativeQuery = true)
    fun getUserCredentialByPhoneNo(phoneNo: String): Optional<UserCredential>

    @Query(
        "SELECT * FROM user_credentials AS uc WHERE uc.phone_no = :phoneNo AND uc.password = :password",
        nativeQuery = true
    )
    fun getUserCredentialByPhoneNoPassword(phoneNo: String, password: String): Optional<UserCredential>
}