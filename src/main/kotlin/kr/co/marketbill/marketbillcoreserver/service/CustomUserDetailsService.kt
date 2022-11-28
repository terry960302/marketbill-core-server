package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.domain.vo.CustomUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService : UserDetailsService{

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val userId = username!!.toLong()
        val credential = userCredentialRepository.getUserCredentialByUserId(userId)

        val hasCred = credential.isPresent
        if (!hasCred) throw Error("There's no user whose userId is $userId")

        return CustomUserDetails(
            phoneNo = credential.get().phoneNo,
            role = AccountRole.values().first { it.name == credential.get().user!!.userCredential!!.role.toString() }
        )
    }
}