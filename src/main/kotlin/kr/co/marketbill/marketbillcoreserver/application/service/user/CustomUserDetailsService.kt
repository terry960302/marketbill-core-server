package kr.co.marketbill.marketbillcoreserver.application.service.user

import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.domain.vo.CustomUserDetails
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException
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
            val cred = userCredentialRepository.getUserCredentialByUserId(userId)
                .orElseThrow {
                    CustomException(
                        message = "There's no user whose userId is $userId",
                        errorType = ErrorType.NOT_FOUND,
                        errorCode = ErrorCode.NO_USER
                    )
                }
            val createdDetails = CustomUserDetails(
                phoneNo = cred.phoneNo,
                role = AccountRole.values()
                    .first { it.name == cred.user!!.userCredential!!.role.toString() },
                password = cred.password,
            )
            return createdDetails
        }
}