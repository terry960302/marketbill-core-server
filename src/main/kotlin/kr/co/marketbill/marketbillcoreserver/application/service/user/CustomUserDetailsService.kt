package kr.co.marketbill.marketbillcoreserver.application.service.user

import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.shared.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.domain.vo.CustomUserDetails
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService : UserDetailsService {

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository
    private val logger: Logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)
    private val className = this.javaClass.simpleName

    override fun loadUserByUsername(username: String?): UserDetails {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val userId = username!!.toLong()
            val credential = userCredentialRepository.getUserCredentialByUserId(userId)

            val hasCred = credential.isPresent
            if (!hasCred) throw CustomException(
                message = "There's no user whose userId is $userId",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_USER
            )
            logger.trace("$className.$executedFunc >> user credential is existed.")

            val createdDetails = CustomUserDetails(
                phoneNo = credential.get().phoneNo,
                role = AccountRole.values()
                    .first { it.name == credential.get().user!!.userCredential!!.role.toString() }
            )
            logger.info("$className.$executedFunc >> completed")
            return createdDetails
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }
}