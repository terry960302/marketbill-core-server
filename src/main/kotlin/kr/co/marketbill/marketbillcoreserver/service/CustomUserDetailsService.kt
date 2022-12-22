package kr.co.marketbill.marketbillcoreserver.service

import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.domain.vo.CustomUserDetails
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.graphql.error.InternalErrorException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService : UserDetailsService {

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val userId = username!!.toLong()
        val credential = userCredentialRepository.getUserCredentialByUserId(userId)

        val hasCred = credential.isPresent
        if (!hasCred) throw CustomException(
            message = "There's no user whose userId is $userId",
            errorType = ErrorType.NOT_FOUND,
            errorCode = CustomErrorCode.NO_USER
        )

        return CustomUserDetails(
            phoneNo = credential.get().phoneNo,
            role = AccountRole.values().first { it.name == credential.get().user!!.userCredential!!.role.toString() }
        )
    }
}