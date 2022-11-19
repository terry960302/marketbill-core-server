package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.data.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.data.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.data.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.data.entity.user.User
import kr.co.marketbill.marketbillcoreserver.data.entity.user.UserCredential
import kr.co.marketbill.marketbillcoreserver.data.repository.user.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.user.BizConnectionRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.user.UserRepository
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.types.SignInInput
import kr.co.marketbill.marketbillcoreserver.types.SignUpInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

@Service
class UserService {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    @Autowired
    private lateinit var authTokenRepository: AuthTokenRepository

    @Autowired
    private lateinit var bizConnectionRepository: BizConnectionRepository

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    companion object {
        const val NO_USER_ERR =
            "There's no user who has this phone number and password. Please check your phone number again."
    }
    val logger: Logger = LoggerFactory.getLogger(UserService::class.java)


    fun me(userId: Long): Optional<User> {
        return userRepository.findById(userId)
    }

    fun getAllBizConnByRetailerId(retailerId: Long): List<BizConnection> {
        return bizConnectionRepository.getAllBizConnByRetailerId(retailerId)
    }

    fun getAllBizConnByWholesalerId(wholesalerId: Long): List<BizConnection> {
        return bizConnectionRepository.getAllBizConnByWholesalerId(wholesalerId)
    }

    @Transactional
    fun signUp(input: SignUpInput): AuthTokenDto {
        try {
            val hasUserCred: Boolean = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo).isPresent
            if (hasUserCred) throw IllegalArgumentException("There's user who has same phone number. Please check your phone number again.")

            val userInput = User(name = input.name, businessNo = null)
            val user = userRepository.save(userInput)

            val userCredInput = UserCredential(
                phoneNo = input.phoneNo,
                password = passwordEncoder.encode(input.password),
                role = AccountRole.valueOf(input.role.name),
                user = user
            )
            userCredentialRepository.save(userCredInput)

            return generateAuthToken(user, role = AccountRole.valueOf(input.role.name)) {
                authTokenRepository.save(it)
            }


        } catch (err: Error) {
            throw Error("Something wrong with this err => ${err.message}")
        }
    }

    @Transactional
    fun signIn(input: SignInInput): AuthTokenDto {
        val userCred = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo)

        val hasUserCred = userCred.isPresent
        if (!hasUserCred) throw IllegalArgumentException(NO_USER_ERR)


        val isValidPassword = passwordEncoder.matches(input.password, userCred.get().password)
        if (!isValidPassword) throw IllegalArgumentException(NO_USER_ERR)
        return generateAuthToken(userCred.get().user!!, role = AccountRole.valueOf(input.role.name)) {
            val authTokenId = userCred.get().user?.authToken?.id
            authTokenRepository.save(
                AuthToken(
                    id = authTokenId,
                    refreshToken = it.refreshToken,
                    user = userCred.get().user,
                )
            )
        }
    }

    fun generateAuthToken(
        user: User,
        role: AccountRole,
        onGenerateRefreshToken: ((input: AuthToken) -> Unit)? = null
    ): AuthTokenDto {
        val accessToken =
            jwtProvider.generateToken(user.id!!, role.toString(), JwtProvider.accessExpiration)
        val refreshToken =
            jwtProvider.generateToken(user.id!!, role.toString(), JwtProvider.refreshExpiration)

        val authTokenInput = AuthToken(
            refreshToken = refreshToken,
            user = user,
        )

        if (onGenerateRefreshToken != null) {
            onGenerateRefreshToken(authTokenInput)
        }

        return AuthTokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @PostConstruct
    fun createMockUsers() {
        val retailer = User(name = "name_retailer", businessNo = null)
        val wholesaler = User(name = "name_wholesaler", businessNo = null)
        userRepository.saveAll(arrayListOf(retailer, wholesaler))

        val retailerCred = UserCredential(
            user = entityManager.getReference(User::class.java, 1.toLong()),
            phoneNo = "01011112222",
            password = passwordEncoder.encode("1234"),
            role = AccountRole.ROLE_RETAILER
        )
        val wholesalerCred = UserCredential(
            user = entityManager.getReference(User::class.java, 2.toLong()),
            phoneNo = "01011113333",
            password = passwordEncoder.encode("1234"),
            role = AccountRole.ROLE_WHOLESALER_EMPR
        )
        userCredentialRepository.saveAll(arrayListOf(retailerCred, wholesalerCred))

        val authToken1 = AuthToken(
            user = entityManager.getReference(User::class.java, 1.toLong()),
            refreshToken = jwtProvider.generateToken(
                1.toLong(),
                AccountRole.ROLE_RETAILER.toString(),
                JwtProvider.refreshExpiration
            )
        )
        val authToken2 = AuthToken(
            user = entityManager.getReference(User::class.java, 2.toLong()),
            refreshToken = jwtProvider.generateToken(
                2.toLong(),
                AccountRole.ROLE_RETAILER.toString(),
                JwtProvider.refreshExpiration
            )
        )
        authTokenRepository.saveAll(arrayListOf(authToken1, authToken2))

        val bizConn = BizConnection(
            retailer = entityManager.getReference(User::class.java, 1.toLong()),
            wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
            applyStatus = ApplyStatus.APPLYING,
        )
        bizConnectionRepository.save(bizConn)

        logger.trace("createMockUsers completed")
    }
}