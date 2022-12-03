package kr.co.marketbill.marketbillcoreserver.service

import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.*
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.*
import kr.co.marketbill.marketbillcoreserver.domain.specs.BizConnSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.UserSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.WholesalerConnSpecs
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
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
import javax.servlet.http.HttpServletResponse

@Service
class UserService {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    @Autowired
    private lateinit var bizConnectionRepository: BizConnectionRepository

    @Autowired
    private lateinit var wholesalerConnectionRepository: WholesalerConnectionRepository

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var messagingService: MessagingService

    @Autowired
    private lateinit var authTokenRepository: AuthTokenRepository

    @Autowired
    private lateinit var tokenService: TokenService

    companion object {
        const val DEFAULT_WHOLESALER_BELONGS_TO = "양재" // 추후 경부선이나 다른 꽃시장명이 추가될 수 있으니 초기에만 하드코딩 처리
        const val NO_USER_ERR =
            "There's no user who has this phone number and password. Please check your phone number again."
        const val NO_TOKEN_WITH_APPLY_STATUS_ERR =
            "There must be token in cookie when using 'applyStatus' field or 'bizConnectionId' field"
        const val EMPLOYEE_SIGN_UP_WITHOUT_EMPLOYER_ERR =
            "Wholesale Employer need to sign up first. Employee could not sign up without employer account."
        const val HAS_BIZ_CONNECTION_ERR = "There's already a bizConnection"
        const val NO_BIZ_CONNECTION_TO_UPDATE_ERR = "There's no bizConnection to update. Please check biz_connection ID"
        const val SAME_PHONE_NO_ERR = "There's user who has same phone number. Please check your phone number again."
        const val SAME_WHOLESALER_NAME_ERR = "There's a wholesale employer who has same company name"
    }

    val logger: Logger = LoggerFactory.getLogger(UserService::class.java)


    fun getUser(userId: Long): Optional<User> {
        return userRepository.findById(userId)
    }

    fun getAllUsers(roles: List<AccountRole>?, pageable: Pageable): Page<User> {
        return userRepository.findAll(UserSpecs.hasRoles(roles), pageable)
    }

    fun getUsersWithApplyStatus(userId: Long?, role: AccountRole?, pageable: Pageable): Page<User> {
        if (userId == null || role == null) {
            throw CustomException(NO_TOKEN_WITH_APPLY_STATUS_ERR)
        }
        if (role == AccountRole.RETAILER) {
            val roles = listOf(AccountRole.WHOLESALER_EMPR) // 사장님과만 거래처 관계 생성 가능
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            return users.map { it ->
                val connections = it.receivedConnections!!.filter { conn -> conn.retailer!!.id == userId }
                if (connections.isNotEmpty()) {
                    it.applyStatus = connections[0].applyStatus
                    it.bizConnectionId = connections[0].id
                }
                it
            }
        } else {
            val roles = listOf<AccountRole>(AccountRole.RETAILER)
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            return users.map { it ->
                val connections = it.appliedConnections!!.filter { conn -> conn.wholesaler!!.id == userId }
                if (connections.isNotEmpty()) {
                    it.applyStatus = connections[0].applyStatus
                    it.bizConnectionId = connections[0].id
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


    /**
     * ### Case. 도매상(직원)
     *      : 도매상 사장이 존재하는가?(같은 업체명의 role=WHOLESALER_EMPR 가 있는가?)
     *      - 있으면 User 만들고 연결.
     *      - 없으면 바로 에러 반환(사장이 가입하지 않은 상태에서 직원 혼자 가입불가)
     *
     * ### Case. 소매상, 도매상(사장)
     *      : 일반 방식으로 가입
     */
    @Transactional
    fun signUp(input: SignUpInput): AuthTokenDto {
        try {
            val isWholesalerEmployee =
                input.role == kr.co.marketbill.marketbillcoreserver.types.AccountRole.WHOLESALER_EMPE


            val user = if (isWholesalerEmployee) {
                val wholesaleEmployers = userRepository.findAll(
                    UserSpecs.hasRoles(listOf(AccountRole.WHOLESALER_EMPR)).and(UserSpecs.isName(input.name))
                )
                if (wholesaleEmployers.isEmpty()) {
                    throw CustomException(EMPLOYEE_SIGN_UP_WITHOUT_EMPLOYER_ERR)
                } else {
                    val employer = wholesaleEmployers[0]
                    val employee = createUser(input)

                    val connection = WholesalerConnection(employer = employer, employee = employee)
                    wholesalerConnectionRepository.save(connection)
                    employee
                }
            } else {
                createUser(input)
            }

            val authToken = tokenService.generateAuthTokenPair(userId = user.id!!, role = AccountRole.valueOf(input.role.name))
            tokenService.upsertAuthToken(user.id!!, authToken)

            return authToken
        } catch (err: Exception) {
            throw err
        }
    }

    @Transactional
    fun signIn(input: SignInInput): AuthTokenDto {
        val userCred = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo)

        val hasUserCred = userCred.isPresent
        if (!hasUserCred) throw CustomException(NO_USER_ERR)

        val isValidPassword = passwordEncoder.matches(input.password, userCred.get().password)
        if (!isValidPassword) throw CustomException(NO_USER_ERR)

        val role = userCred.get().role
        val userId = userCred.get().user!!.id!!

        val authToken = tokenService.generateAuthTokenPair(userId, AccountRole.valueOf(role.toString()))
        tokenService.upsertAuthToken(userId, authToken)
        return authToken
    }


    fun createBizConnection(retailerId: Long, wholesalerId: Long): BizConnection {
        val bizConnections: List<BizConnection> = bizConnectionRepository.findAll(
            BizConnSpecs.isRetailerId(retailerId).and(BizConnSpecs.isWholesalerId(wholesalerId))
        )
        if (bizConnections.isNotEmpty()) throw CustomException(HAS_BIZ_CONNECTION_ERR)

        val bizConnection = BizConnection(
            retailer = entityManager.getReference(User::class.java, retailerId),
            wholesaler = entityManager.getReference(User::class.java, wholesalerId),
            applyStatus = ApplyStatus.APPLYING,
        )

        val retailer = userRepository.findById(retailerId)
        val wholesaler = userRepository.findById(wholesalerId)
        if (wholesaler.isEmpty) throw CustomException("There's no user(wholesaler) that you hope to connect with.")
        val retailerName = retailer.get().name!!
        val targetPhoneNo = wholesaler.get().userCredential!!.phoneNo
        val url = ""

        runBlocking {
            messagingService.sendApplyBizConnectionSMS(targetPhoneNo, retailerName, url)
        }
        return bizConnectionRepository.save(bizConnection)
    }

    fun updateBizConnection(bizConnId: Long, status: ApplyStatus): BizConnection {
        val bizConnection: Optional<BizConnection> = bizConnectionRepository.findById(bizConnId)
        if (bizConnection.isEmpty) throw CustomException(NO_BIZ_CONNECTION_TO_UPDATE_ERR)

        bizConnection.get().applyStatus = status
        val updatedBizConn = bizConnectionRepository.save(bizConnection.get())

        val retailer = bizConnection.get().retailer
        val wholesaler = bizConnection.get().wholesaler
        val targetPhoneNo = retailer!!.userCredential!!.phoneNo
        val wholesalerName = wholesaler!!.name!!

        when (status) {
            ApplyStatus.APPLYING -> {
                logger.info("No need messaging API call on APPLYING status")
            }
            ApplyStatus.CONFIRMED -> {
                val url = ""
                runBlocking {
                    messagingService.sendConfirmBizConnectionSMS(
                        to = targetPhoneNo,
                        wholesalerName = wholesalerName,
                        url = url
                    )
                }
            }
            ApplyStatus.REJECTED -> {
                runBlocking {
                    messagingService.sendRejectBizConnectionSMS(
                        to = targetPhoneNo,
                        wholesalerName = wholesalerName,
                    )
                }
            }
        }
        return updatedBizConn
    }

    @Transactional
    fun createUser(input: SignUpInput): User {
        val hasUserCred: Boolean = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo).isPresent
        if (hasUserCred) throw CustomException(SAME_PHONE_NO_ERR)

        if (input.role == kr.co.marketbill.marketbillcoreserver.types.AccountRole.WHOLESALER_EMPR) {
            val hasSameEmployer = userRepository.findAll(
                UserSpecs.hasRoles(listOf(AccountRole.WHOLESALER_EMPR)).and(UserSpecs.isName(input.name))
            ).size > 0
            if (hasSameEmployer) throw CustomException(SAME_WHOLESALER_NAME_ERR)
        }

        val belongsTo = when (AccountRole.valueOf(input.role.toString())) {
            AccountRole.RETAILER -> null
            AccountRole.WHOLESALER_EMPR -> DEFAULT_WHOLESALER_BELONGS_TO
            AccountRole.WHOLESALER_EMPE -> DEFAULT_WHOLESALER_BELONGS_TO
        }

        val user = User(
            name = input.name,
            businessNo = null,
            belongsTo = belongsTo,
        )
        val savedUser = userRepository.save(user)

        val userCred = UserCredential(
            phoneNo = input.phoneNo,
            password = passwordEncoder.encode(input.password),
            role = AccountRole.valueOf(input.role.name),
            user = savedUser
        )
        userCredentialRepository.save(userCred)
        return savedUser
    }

    fun signOut(userId: Long) {
        val authToken = authTokenRepository.findByUserId(userId)
        if (authToken.isPresent) {
            authTokenRepository.deleteById(authToken.get().id!!)
        }
//        jwtProvider.resetAllTokensInHttpOnlyCookie(response)
        return
    }



    fun getConnectedEmployerId(employeeId: Long): Long {
        try {
            val connections = wholesalerConnectionRepository.findAll(WholesalerConnSpecs.byEmployeeId(employeeId))
            if (connections.isEmpty()) throw Error()
            val employer = connections[0].employer!!
            return employer.id!!
        } catch (e: Exception) {
            throw CustomException("There's no employer in same wholesale company.")
        }
    }
}