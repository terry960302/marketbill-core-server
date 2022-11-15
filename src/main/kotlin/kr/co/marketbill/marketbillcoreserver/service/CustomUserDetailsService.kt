package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.repository.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.vo.CustomUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService : UserDetailsService{

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val userId = username!!.toLong() // userId를 받아서 처리할 예정
        val credential = userCredentialRepository.getUserCredentialByUserId(userId)

        val userDetails = CustomUserDetails(
            phoneNo = credential.get().phoneNo,
            role = AccountRole.values().first { it.name == credential.get().user!!.userCredential!!.role.toString() }
        )

        if (credential.isEmpty) {
            throw Error("There's no user mathed to username")
        } else {
            return userDetails
        }
    }
}