package kr.co.marketbill.marketbillcoreserver.application.service.user

import com.netflix.graphql.types.errors.ErrorType
import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.application.dto.response.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.shared.constants.*
import kr.co.marketbill.marketbillcoreserver.application.service.common.MessagingService
import kr.co.marketbill.marketbillcoreserver.application.service.user.TokenService
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.*
import kr.co.marketbill.marketbillcoreserver.domain.specs.BizConnSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.UserSpecs
import kr.co.marketbill.marketbillcoreserver.domain.specs.WholesalerConnSpecs
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.*
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException
import kr.co.marketbill.marketbillcoreserver.shared.util.groupFillBy
import kr.co.marketbill.marketbillcoreserver.types.CreateBusinessInfoInput
import kr.co.marketbill.marketbillcoreserver.types.SignInInput
import kr.co.marketbill.marketbillcoreserver.types.SignUpInput
import kr.co.marketbill.marketbillcoreserver.types.UpdatePasswordInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kr.co.marketbill.marketbillcoreserver.domain.validator.PasswordValidator
import kr.co.marketbill.marketbillcoreserver.domain.validator.UrlValidator
import java.util.*
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
    private lateinit var businessInfoRepository: BusinessInfoRepository

    @Autowired
    private lateinit var tokenService: TokenService

    companion object {
        const val DEFAULT_WHOLESALER_BELONGS_TO = "양재" // 추후 경부선이나 다른 꽃시장명이 추가될 수 있으니 초기에만 하드코딩 처리
        const val NO_USER_ERR =
            "There's no user who has this phone number and password. Please check your phone number again."
        const val NO_TOKEN_WITH_APPLY_STATUS_ERR =
            "There must be token in header when using 'applyStatus' field or 'bizConnectionId' field"
        const val EMPLOYEE_SIGN_UP_WITHOUT_EMPLOYER_ERR =
            "Wholesale Employer need to sign up first. Employee could not sign up without employer account."
        const val HAS_BIZ_CONNECTION_ERR = "There's already a bizConnection"
        const val NO_BIZ_CONNECTION_TO_UPDATE_ERR = "There's no bizConnection to update. Please check biz_connection ID"
        const val SAME_PHONE_NO_ERR = "There's user who has same phone number. Please check your phone number again."
        const val SAME_WHOLESALER_NAME_ERR = "There's a wholesale employer who has same company name"
    }

    private val logger: Logger = LoggerFactory.getLogger(UserService::class.java)
    private val className: String = this.javaClass.simpleName


    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        val user: Optional<User> = userRepository.findById(userId)
        if (user.isEmpty) {
            val msg = "There's no user whose id is $userId"
            logger.error("$className.$executedFunc >> $msg")
            throw CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_USER
            )
        } else {
            logger.info("$className.$executedFunc >> completed.")
            return user.get()
        }
    }

    @Transactional(readOnly = true)
    fun getUsers(roles: List<AccountRole>?, phoneNo: String?, name: String?, pageable: Pageable): Page<User> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val users = userRepository.findAll(
                UserSpecs.hasRoles(roles)
                    .and(UserSpecs.likeName(name))
                    .and(UserSpecs.byPhoneNo(phoneNo)), pageable
            )
            logger.info("$className.$executedFunc >> completed.")
            return users
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getUsersWithApplyStatus(userId: Long?, role: AccountRole?, pageable: Pageable): Page<User> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        if (userId == null || role == null) {
            val msg = NO_TOKEN_WITH_APPLY_STATUS_ERR
            logger.error("$className.$executedFunc >> $msg")
            throw CustomException(
                message = msg,
                errorType = ErrorType.UNAUTHENTICATED,
                errorCode = CustomErrorCode.TOKEN_NEEDED
            )
        }
        if (role == AccountRole.RETAILER) {
            val roles = listOf(AccountRole.WHOLESALER_EMPR) // 사장님과만 거래처 관계 생성 가능
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            val usersWithApplyStatus = users.map {
                val connections = it.receivedConnections.filter { conn -> conn.retailer!!.id == userId }
                if (connections.isNotEmpty()) {
                    it.applyStatus = connections[0].applyStatus
                    it.bizConnectionId = connections[0].id
                }
                it
            }
            logger.info("$className.$executedFunc >> completed.")
            return usersWithApplyStatus
        } else {
            val roles = listOf<AccountRole>(AccountRole.RETAILER)
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            val usersWithApplyStatus = users.map {
                val connections = it.appliedConnections.filter { conn -> conn.wholesaler!!.id == userId }
                if (connections.isNotEmpty()) {
                    it.applyStatus = connections[0].applyStatus
                    it.bizConnectionId = connections[0].id
                }
                it
            }
            logger.info("$className.$executedFunc >> completed.")
            return usersWithApplyStatus
        }
    }

    @Transactional
    fun removeUser(userId: Long) {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val user: Optional<User> = userRepository.findById(userId)
            if (user.isEmpty) {
                val msg = "There's no user data want to delete"
                logger.error("$className.$executedFunc >> $msg")
                throw CustomException(
                    message = msg,
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_USER
                )
            }
            userRepository.deleteById(userId)
            logger.info("$className.$executedFunc >> completed.")
        } catch (e: Exception) {
            logger.info("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun updatePassword(input: UpdatePasswordInput) {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val isValidPassword = PasswordValidator.isValid(input.password)
            if (!isValidPassword) {
                throw CustomException(
                    message = "Invalid format of password. Password must be at least 8 letters including english, number, special characters with no whitespaces.",
                    errorType = ErrorType.BAD_REQUEST,
                    errorCode = CustomErrorCode.INVALID_FORMAT,
                )
            }
            logger.info("$className.$executedFunc >> password for updating is validated.")

            val user: Optional<User> = userRepository.findById(input.userId.toLong())
            if (user.isEmpty) {
                throw CustomException(
                    message = "There's no user whose id is ${input.userId}",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_USER
                )
            }
            logger.info("$className.$executedFunc >> user(password owner) is existed.")


            val phoneNo: String = user.get().userCredential!!.phoneNo
            if (phoneNo != input.phoneNo) {
                throw CustomException(
                    message = "Phone Number(by DB) and Phone Number(by input) is not matched. Please check phone number that you use when sign up.",
                    errorType = ErrorType.BAD_REQUEST,
                    errorCode = CustomErrorCode.INVALID_DATA
                )
            }
            logger.info("$className.$executedFunc >> DB phoneNo data and input phoneNo data is matched.")

            val credential = user.get().userCredential
            credential!!.password = passwordEncoder.encode(input.password)
            userCredentialRepository.save(credential)
            logger.info("$className.$executedFunc >> completed.")
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    fun getAppliedConnectionsByRetailerIds(
        retailerIds: List<Long>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): MutableMap<Long, List<BizConnection>> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val bizConnections = bizConnectionRepository.findAll(
                BizConnSpecs.hasApplyStatus(status).and(BizConnSpecs.byRetailerIds(retailerIds)), pageable
            )
            val groupedBizConns = bizConnections.groupFillBy(retailerIds) { it.retailer!!.id!! }.toMutableMap()
            logger.info("$className.$executedFunc >> completed.")
            return groupedBizConns
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }

    }

    fun getReceivedConnectionsByWholesalerIds(
        wholesalerIds: List<Long>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): MutableMap<Long, List<BizConnection>> {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val bizConnections = bizConnectionRepository.findAll(
                BizConnSpecs.hasApplyStatus(status).and(BizConnSpecs.byWholesalerIds(wholesalerIds)), pageable
            )
            val groupedBizConns = bizConnections.groupFillBy(wholesalerIds) { it.wholesaler!!.id!! }.toMutableMap()
            logger.debug("$className.$executedFunc >> ${groupedBizConns.mapValues { it.value.map { it.id } }}")
            logger.info("$className.$executedFunc >> completed.")
            return groupedBizConns
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun upsertBusinessInfo(input: CreateBusinessInfoInput): BusinessInfo {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        val isValidStampUrl = UrlValidator.isValid(input.sealStampImgUrl)
        if (!isValidStampUrl) {
            val msg = "Invalid seal stamp img url. Please check url format."
            logger.error("$className.$executedFunc >> $msg")
            throw CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.INVALID_FORMAT
            )
        }
        logger.info("$className.$executedFunc >> stamp url validated.")

        val user = userRepository.findById(input.userId.toLong())
        if (user.isEmpty) {
            val msg = "There's no user whose ID is ${input.userId}"
            logger.error("$className.$executedFunc >> $msg")
            throw CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_USER
            )
        }
        logger.info("$className.$executedFunc >> userID(${input.userId}) is existed.")

        val newBusinessInfo = BusinessInfo(
            user = entityManager.getReference(User::class.java, input.userId.toLong()),
            companyName = input.companyName,
            companyPhoneNo = input.companyPhoneNo,
            businessMainCategory = input.businessMainCategory,
            businessSubCategory = input.businessSubCategory,
            employerName = input.employerName,
            businessNo = input.businessNo,
            sealStampImgUrl = input.sealStampImgUrl,
            bankAccount = input.bankAccount,
            address = input.address
        )
        val prevBusinessInfo = businessInfoRepository.findByUserId(input.userId.toLong())
        if (prevBusinessInfo.isPresent) {
            newBusinessInfo.id = prevBusinessInfo.get().id
        }
        logger.info("$className.$executedFunc >> businessInfo object is created.")

        val upsertedBusinessInfo = businessInfoRepository.save(newBusinessInfo)
        logger.info("$className.$executedFunc >> completed.")
        return upsertedBusinessInfo
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
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name
        try {
            val isWholesalerEmployee =
                input.role == kr.co.marketbill.marketbillcoreserver.types.AccountRole.WHOLESALER_EMPE

            val user = if (isWholesalerEmployee) {
                wholesalerEmployeeSignUp(input)
            } else {
                wholesaleEmployerAndRetailerSignUp(input)
            }

            val authToken =
                tokenService.generateAuthTokenPair(userId = user.id!!, role = AccountRole.valueOf(input.role.name))
            tokenService.upsertAuthToken(user.id!!, authToken)
            logger.info("$className.$executedFunc >> auth token generated.")
            logger.info("$className.$executedFunc >> completed.")

            return authToken
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun wholesalerEmployeeSignUp(input: SignUpInput): User {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        val wholesaleEmployers: List<User> = userRepository.findAll(
            UserSpecs.hasRoles(listOf(AccountRole.WHOLESALER_EMPR)).and(UserSpecs.isName(input.name))
        )
        val hasSameNameEmployer = wholesaleEmployers.isNotEmpty()
        if (!hasSameNameEmployer) {
            throw CustomException(
                message = EMPLOYEE_SIGN_UP_WITHOUT_EMPLOYER_ERR,
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.EMPLOYER_SIGNUP_NEEDED
            )
        }

        val employer = wholesaleEmployers[0]
        val employee = createUser(input)

        val connection = WholesalerConnection(employer = employer, employee = employee)
        wholesalerConnectionRepository.save(connection)
        logger.info("$className.$executedFunc >> wholesaler connection is created.")
        return employee
    }

    @Transactional
    fun wholesaleEmployerAndRetailerSignUp(input: SignUpInput): User {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        val hasSameNameUser = userRepository.findAll(UserSpecs.isName(input.name)).isNotEmpty()
        if (hasSameNameUser) {
            val msg = SAME_WHOLESALER_NAME_ERR
            logger.error("$className.$executedFunc >> $msg")
            throw CustomException(
                message = msg,
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.USER_NAME_DUPLICATED
            )
        }
        logger.info("$className.$executedFunc >> user of same name validated.")

        return createUser(input)
    }


    @Transactional
    fun signIn(input: SignInInput): AuthTokenDto {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val userCred = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo)
            val hasUserCred = userCred.isPresent
            if (!hasUserCred) throw CustomException(
                message = NO_USER_ERR,
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_USER
            )
            logger.info("$className.$executedFunc >> user credential by phoneNo checked.")

            val isValidPassword = passwordEncoder.matches(input.password, userCred.get().password)
            if (!isValidPassword) throw CustomException(
                message = NO_USER_ERR,
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_USER
            )
            logger.info("$className.$executedFunc >> password validated.")

            val role = userCred.get().role
            val userId = userCred.get().user!!.id!!

            val authToken = tokenService.generateAuthTokenPair(userId, AccountRole.valueOf(role.toString()))
            tokenService.upsertAuthToken(userId, authToken)
            logger.info("$className.$executedFunc >> auth token generated.")
            logger.info("$className.$executedFunc >> completed.")
            return authToken
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun createBizConnection(retailerId: Long, wholesalerId: Long): BizConnection {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val bizConnections: List<BizConnection> = bizConnectionRepository.findAll(
                BizConnSpecs.isRetailerId(retailerId).and(BizConnSpecs.isWholesalerId(wholesalerId))
            )
            val validBizConnections = bizConnections.filter { it.applyStatus != ApplyStatus.REJECTED }
            if (bizConnections.isNotEmpty() && validBizConnections.isNotEmpty()) {
                throw CustomException(
                    message = HAS_BIZ_CONNECTION_ERR,
                    errorType = ErrorType.INTERNAL,
                    errorCode = CustomErrorCode.BIZ_CONNECTION_DUPLICATED
                )
            }
            logger.info("$className.$executedFunc >> existed biz_connections is validated. ready to create new biz_connection.")

            val bizConnection = BizConnection(
                retailer = entityManager.getReference(User::class.java, retailerId),
                wholesaler = entityManager.getReference(User::class.java, wholesalerId),
                applyStatus = ApplyStatus.APPLYING,
            )

            val retailer = userRepository.findById(retailerId)
            val wholesaler = userRepository.findById(wholesalerId)
            if (wholesaler.isEmpty) {
                throw CustomException(
                    message = "There's no user(wholesaler) that you hope to connect with.",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = CustomErrorCode.NO_USER
                )
            }
            if (wholesaler.get().userCredential!!.role == AccountRole.WHOLESALER_EMPE) {
                throw CustomException(
                    message = "You cannot make business connection with employee(wholesaler).",
                    errorType = ErrorType.BAD_REQUEST,
                    errorCode = CustomErrorCode.INVALID_DATA
                )
            }
            val retailerName = retailer.get().name!!
            val targetPhoneNo = wholesaler.get().userCredential!!.phoneNo
            logger.info("$className.$executedFunc >> bizConnection object is created.")

            runBlocking {
                messagingService.sendApplyBizConnectionSMS(targetPhoneNo, retailerName)
            }
            logger.info("$className.$executedFunc >> sent bizConnection message.")

            val createdBizConn = bizConnectionRepository.save(bizConnection)
            logger.info("$className.$executedFunc >> completed.")
            return createdBizConn
        } catch (e: Exception) {
            throw e
        }
    }

    @Transactional
    fun updateBizConnection(bizConnId: Long, status: ApplyStatus): BizConnection {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val bizConnection: Optional<BizConnection> = bizConnectionRepository.findById(bizConnId)
            if (bizConnection.isEmpty) throw CustomException(
                message = NO_BIZ_CONNECTION_TO_UPDATE_ERR,
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_BIZ_CONNECTION
            )
            logger.info("$className.$executedFunc >> bizConnection to update is existed.")

            bizConnection.get().applyStatus = status
            val updatedBizConn = bizConnectionRepository.save(bizConnection.get())
            logger.info("$className.$executedFunc >> bizConnection is updated.")

            val retailer = bizConnection.get().retailer
            val wholesaler = bizConnection.get().wholesaler
            val targetPhoneNo = retailer!!.userCredential!!.phoneNo
            val wholesalerName = wholesaler!!.name!!

            when (status) {
                ApplyStatus.APPLYING -> {
                    logger.info("No need messaging API call on APPLYING status")
                }
                ApplyStatus.CONFIRMED -> {
                    runBlocking {
                        messagingService.sendConfirmBizConnectionSMS(
                            to = targetPhoneNo,
                            wholesalerName = wholesalerName,
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
            logger.info("$className.$executedFunc >> sent bizConnection status updated message.")
            logger.info("$className.$executedFunc >> completed.")

            return updatedBizConn
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    @Transactional
    fun createUser(input: SignUpInput): User {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        val hasUserCred: Boolean = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo).isPresent
        if (hasUserCred) {
            val msg = SAME_PHONE_NO_ERR
            logger.error("$className.$executedFunc >> $msg")
            throw CustomException(
                message = msg,
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.PHONE_NO_DUPLICATED
            )
        }
        logger.info("$className.$executedFunc >> checked same user credential info.")

        val belongsTo = when (AccountRole.valueOf(input.role.toString())) {
            AccountRole.RETAILER -> null
            AccountRole.WHOLESALER_EMPR -> DEFAULT_WHOLESALER_BELONGS_TO
            AccountRole.WHOLESALER_EMPE -> DEFAULT_WHOLESALER_BELONGS_TO
        }

        val user = User(
            name = input.name,
            belongsTo = belongsTo,
        )
        val savedUser = userRepository.save(user)
        logger.info("$className.$executedFunc >> new user created.")

        val userCred = UserCredential(
            phoneNo = input.phoneNo,
            password = passwordEncoder.encode(input.password),
            role = AccountRole.valueOf(input.role.name),
            user = savedUser
        )
        userCredentialRepository.save(userCred)
        logger.info("$className.$executedFunc >> new user credential created.")
        logger.info("$className.$executedFunc >> completed.")
        return savedUser
    }

    @Transactional
    fun signOut(userId: Long) {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        val authToken = authTokenRepository.findByUserId(userId)
        if (authToken.isPresent) {
            authTokenRepository.deleteById(authToken.get().id!!)
            logger.info("$className.$executedFunc >> completed.")
        } else {
            val msg = "There's no user to sign out."
            logger.error("$className.$executedFunc >> $msg")
            throw CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_USER
            )
        }
    }

    @Transactional
    fun getConnectedEmployerId(employeeId: Long): Long {
        val executedFunc = object : Any() {}.javaClass.enclosingMethod.name

        try {
            val connections = wholesalerConnectionRepository.findAll(WholesalerConnSpecs.byEmployeeId(employeeId))
            if (connections.isEmpty()) throw CustomException(
                message = "There's no connection data between employer and employees",
                errorType = ErrorType.NOT_FOUND,
                errorCode = CustomErrorCode.NO_WHOLESALE_CONNECTION
            )
            val employer = connections[0].employer!!
            logger.info("$className.$executedFunc >> completed.")
            return employer.id!!
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}")
            throw e
        }
    }

    
}