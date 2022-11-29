package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.UserCredential
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.BizConnectionRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.UserRepository
import kr.co.marketbill.marketbillcoreserver.domain.specs.BizConnSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.UserSpecs
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.types.SignInInput
import kr.co.marketbill.marketbillcoreserver.types.SignUpInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
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


    fun getUser(userId: Long): Optional<User> {
        return userRepository.findById(userId)
    }

    fun getAllUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    fun getUsersWithApplyStatus(userId: Long?, role: AccountRole?, pageable: Pageable): Page<User> {
        if(userId == null || role == null){
            throw Exception("There must be Authorization token in request header when using 'needApplyStatus' input value or fetching 'applyStatus' field")
        }
        if (role == AccountRole.RETAILER) {
            val roles = listOf<AccountRole>(AccountRole.WHOLESALER_EMPR, AccountRole.WHOLESALER_EMPE)
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            return users.map { it ->
                val connections = it.receivedConnections!!.filter { conn -> conn.retailer!!.id == userId }
                if(connections.isNotEmpty()){
                    it.applyStatus = connections[0].applyStatus
                }
                it
            }
        } else {
            val roles = listOf<AccountRole>(AccountRole.RETAILER)
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            return users.map { it ->
                val connections = it.appliedConnections!!.filter { conn -> conn.wholesaler!!.id == userId }
                if(connections.isNotEmpty()){
                    it.applyStatus = connections[0].applyStatus
                }
                it
            }
        }
    }

    @Transactional(readOnly = true)
    fun getAppliedConnectionsByRetailerIds(
        retailerIds: List<Long>,
        status: ApplyStatus?,
        pageable: Pageable
    ): MutableMap<Long, List<BizConnection>> {
        val bizConnections = bizConnectionRepository.findAll(
            BizConnSpecs.isApplyStatus(status).and(BizConnSpecs.byRetailerIds(retailerIds)), pageable
        )
        return bizConnections.groupBy { it.retailer!!.id!! }.toMutableMap()
    }

    @Transactional(readOnly = true)
    fun getReceivedConnectionsByWholesalerIds(
        wholesalerIds: List<Long>,
        status: ApplyStatus?,
        pageable: Pageable
    ): MutableMap<Long, List<BizConnection>> {
        val bizConnections = bizConnectionRepository.findAll(
            BizConnSpecs.isApplyStatus(status).and(BizConnSpecs.byWholesalerIds(wholesalerIds)), pageable
        )
        return bizConnections.groupBy { it.wholesaler!!.id!! }.toMutableMap()
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

    fun createBizConnection(retailerId: Long, wholesalerId: Long): BizConnection {
        val bizConnections: List<BizConnection> = bizConnectionRepository.findAll(
            BizConnSpecs.isRetailerId(retailerId).and(BizConnSpecs.isWholesalerId(wholesalerId))
        )
        if (bizConnections.isNotEmpty()) throw Exception("There's already a bizConnection between retailer($retailerId) and wholesaler($wholesalerId)")

        val bizConnection = BizConnection(
            retailer = entityManager.getReference(User::class.java, retailerId),
            wholesaler = entityManager.getReference(User::class.java, wholesalerId),
            applyStatus = ApplyStatus.APPLYING,
        )
        return bizConnectionRepository.save(bizConnection)
    }

    fun updateBizConnection(bizConnId: Long, status: ApplyStatus): BizConnection {
        val bizConnection: Optional<BizConnection> = bizConnectionRepository.findById(bizConnId)
        if (bizConnection.isEmpty) throw Exception("There's no bizConnection whose id is $bizConnId")

        bizConnection.get().applyStatus = status
        return bizConnectionRepository.save(bizConnection.get())
    }
}