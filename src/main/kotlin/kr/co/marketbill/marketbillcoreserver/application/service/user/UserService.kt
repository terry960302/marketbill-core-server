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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URL
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



    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        val user = userRepository.findById(userId).orElseThrow {
            val msg = "There's no user whose id is $userId"
            CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.NO_USER
            )
        }
        return user
    }

    @Transactional(readOnly = true)
    fun getUsers(roles: List<AccountRole>?, phoneNo: String?, name: String?, pageable: Pageable): Page<User> {
            val users = userRepository.findAll(
                UserSpecs.hasRoles(roles)
                    .and(UserSpecs.likeName(name))
                    .and(UserSpecs.byPhoneNo(phoneNo)), pageable
            )
            return users
        }
    }

    @Transactional(readOnly = true)
    fun getUsersWithApplyStatus(userId: Long?, role: AccountRole?, pageable: Pageable): Page<User> {
        if (userId == null || role == null) {
            val msg = NO_TOKEN_WITH_APPLY_STATUS_ERR
            throw CustomException(
                message = msg,
                errorType = ErrorType.UNAUTHENTICATED,
                errorCode = ErrorCode.TOKEN_NEEDED
            )
        }
        if (role == AccountRole.RETAILER) {
            val roles = listOf(AccountRole.WHOLESALER_EMPR) // 사장님과만 거래처 관계 생성 가능
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            val usersWithApplyStatus = users.map {
                val connections = it.receivedConnections.filter { conn -> conn.retailer!!.id == userId }
                if (connections.isNotEmpty()) {
                    it.updateApplyInfo(connections[0].applyStatus, connections[0].id)
                }
                it
            }
            return usersWithApplyStatus
        } else {
            val roles = listOf<AccountRole>(AccountRole.RETAILER)
            val users = userRepository.findAll(UserSpecs.hasRoles(roles).and(UserSpecs.exclude(userId)), pageable)

            val usersWithApplyStatus = users.map {
                val connections = it.appliedConnections.filter { conn -> conn.wholesaler!!.id == userId }
                if (connections.isNotEmpty()) {
                    it.updateApplyInfo(connections[0].applyStatus, connections[0].id)
                }
                it
            }
            return usersWithApplyStatus
        }
    }

    @Transactional
    fun removeUser(userId: Long) {
            val user: Optional<User> = userRepository.findById(userId)
            if (user.isEmpty) {
                val msg = "There's no user data want to delete"
                throw CustomException(
                    message = msg,
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_USER
                )
            }
            userRepository.deleteById(userId)
        }
    }

    @Transactional
    fun updatePassword(input: UpdatePasswordInput) {

            val isValidPassword = validatePassword(input.password)
            if (!isValidPassword) {
                throw CustomException(
                    message = "Invalid format of password. Password must be at least 8 letters including english, number, special characters with no whitespaces.",
                    errorType = ErrorType.BAD_REQUEST,
                    errorCode = ErrorCode.INVALID_FORMAT,
                )
            }

            val user = userRepository.findById(input.userId.toLong()).orElseThrow {
                CustomException(
                    message = "There's no user whose id is ${input.userId}",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_USER
                )
            }

            val phoneNo: String = user.userCredential!!.phoneNo
            if (phoneNo != input.phoneNo) {
                throw CustomException(
                    message = "Phone Number(by DB) and Phone Number(by input) is not matched. Please check phone number that you use when sign up.",
                    errorType = ErrorType.BAD_REQUEST,
                    errorCode = ErrorCode.INVALID_DATA
                )
            }

            val credential = user.userCredential
            credential!!.updatePassword(passwordEncoder.encode(input.password))
            userCredentialRepository.save(credential)
        }
    }

    fun getAppliedConnectionsByRetailerIds(
        retailerIds: List<Long>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): MutableMap<Long, List<BizConnection>> {
            val bizConnections = bizConnectionRepository.findAll(
                BizConnSpecs.hasApplyStatus(status).and(BizConnSpecs.byRetailerIds(retailerIds)), pageable
            )
            val groupedBizConns = bizConnections.groupFillBy(retailerIds) { it.retailer!!.id!! }.toMutableMap()
            return groupedBizConns
        }

    }

    fun getReceivedConnectionsByWholesalerIds(
        wholesalerIds: List<Long>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): MutableMap<Long, List<BizConnection>> {
            val bizConnections = bizConnectionRepository.findAll(
                BizConnSpecs.hasApplyStatus(status).and(BizConnSpecs.byWholesalerIds(wholesalerIds)), pageable
            )
            val groupedBizConns = bizConnections.groupFillBy(wholesalerIds) { it.wholesaler!!.id!! }.toMutableMap()
            return groupedBizConns
        }
    }

    @Transactional
    fun upsertBusinessInfo(input: CreateBusinessInfoInput): BusinessInfo {

        val isValidStampUrl = validateUrl(input.sealStampImgUrl)
        if (!isValidStampUrl) {
            val msg = "Invalid seal stamp img url. Please check url format."
            throw CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.INVALID_FORMAT
            )
        }

        val user = userRepository.findById(input.userId.toLong())
        if (user.isEmpty) {
            val msg = "There's no user whose ID is ${input.userId}"
            throw CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.NO_USER
            )
        }

        val newBusinessInfo = BusinessInfo.create(
            user = entityManager.getReference(User::class.java, input.userId.toLong()),
            companyName = input.companyName,
            companyPhoneNo = input.companyPhoneNo,
            businessMainCategory = input.businessMainCategory,
            businessSubCategory = input.businessSubCategory,
            employerName = input.employerName,
            businessNo = input.businessNo,
            sealStampImgUrl = input.sealStampImgUrl,
            bankAccount = input.bankAccount,
            address = input.address,
        )
        businessInfoRepository.findByUserId(input.userId.toLong())
            .ifPresent { newBusinessInfo.assignId(it.id) }

        val upsertedBusinessInfo = businessInfoRepository.save(newBusinessInfo)
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

            return authToken
        }
    }

    @Transactional
    fun wholesalerEmployeeSignUp(input: SignUpInput): User {

        val wholesaleEmployers: List<User> = userRepository.findAll(
            UserSpecs.hasRoles(listOf(AccountRole.WHOLESALER_EMPR)).and(UserSpecs.isName(input.name))
        )
        val hasSameNameEmployer = wholesaleEmployers.isNotEmpty()
        if (!hasSameNameEmployer) {
            throw CustomException(
                message = EMPLOYEE_SIGN_UP_WITHOUT_EMPLOYER_ERR,
                errorType = ErrorType.INTERNAL,
                errorCode = ErrorCode.EMPLOYER_SIGNUP_NEEDED
            )
        }

        val employer = wholesaleEmployers[0]
        val employee = createUser(input)

        val connection = WholesalerConnection(employer = employer, employee = employee)
        wholesalerConnectionRepository.save(connection)
        return employee
    }

    @Transactional
    fun wholesaleEmployerAndRetailerSignUp(input: SignUpInput): User {

        val hasSameNameUser = userRepository.findAll(UserSpecs.isName(input.name)).isNotEmpty()
        if (hasSameNameUser) {
            val msg = SAME_WHOLESALER_NAME_ERR
            throw CustomException(
                message = msg,
                errorType = ErrorType.INTERNAL,
                errorCode = ErrorCode.USER_NAME_DUPLICATED
            )
        }

        return createUser(input)
    }


    @Transactional
    fun signIn(input: SignInInput): AuthTokenDto {

            val userCred = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo)
                .orElseThrow {
                    CustomException(
                        message = NO_USER_ERR,
                        errorType = ErrorType.NOT_FOUND,
                        errorCode = ErrorCode.NO_USER
                    )
                }

            val isValidPassword = passwordEncoder.matches(input.password, userCred.password)
            if (!isValidPassword) throw CustomException(
                message = NO_USER_ERR,
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.NO_USER
            )

            val role = userCred.role
            val userId = userCred.user!!.id!!

            val authToken = tokenService.generateAuthTokenPair(userId, AccountRole.valueOf(role.toString()))
            tokenService.upsertAuthToken(userId, authToken)
            return authToken
        }
    }

    @Transactional
    fun createBizConnection(retailerId: Long, wholesalerId: Long): BizConnection {

            val bizConnections: List<BizConnection> = bizConnectionRepository.findAll(
                BizConnSpecs.isRetailerId(retailerId).and(BizConnSpecs.isWholesalerId(wholesalerId))
            )
            val validBizConnections = bizConnections.filter { it.applyStatus != ApplyStatus.REJECTED }
            if (bizConnections.isNotEmpty() && validBizConnections.isNotEmpty()) {
                throw CustomException(
                    message = HAS_BIZ_CONNECTION_ERR,
                    errorType = ErrorType.INTERNAL,
                    errorCode = ErrorCode.BIZ_CONNECTION_DUPLICATED
                )
            }

            val bizConnection = BizConnection(
                retailer = entityManager.getReference(User::class.java, retailerId),
                wholesaler = entityManager.getReference(User::class.java, wholesalerId),
                applyStatus = ApplyStatus.APPLYING,
            )

            val retailer = userRepository.findById(retailerId).orElseThrow {
                CustomException(
                    message = "There's no user whose id is $retailerId",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_USER
                )
            }
            val wholesaler = userRepository.findById(wholesalerId).orElseThrow {
                CustomException(
                    message = "There's no user(wholesaler) that you hope to connect with.",
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_USER
                )
            }
            if (wholesaler.userCredential!!.role == AccountRole.WHOLESALER_EMPE) {
                throw CustomException(
                    message = "You cannot make business connection with employee(wholesaler).",
                    errorType = ErrorType.BAD_REQUEST,
                    errorCode = ErrorCode.INVALID_DATA
                )
            }
            val retailerName = retailer.name!!
            val targetPhoneNo = wholesaler.userCredential!!.phoneNo

            runBlocking {
                messagingService.sendApplyBizConnectionSMS(targetPhoneNo, retailerName)
            }

            val createdBizConn = bizConnectionRepository.save(bizConnection)
            return createdBizConn
        }
    }

    @Transactional
    fun updateBizConnection(bizConnId: Long, status: ApplyStatus): BizConnection {

            val bizConnection = bizConnectionRepository.findById(bizConnId).orElseThrow {
                CustomException(
                    message = NO_BIZ_CONNECTION_TO_UPDATE_ERR,
                    errorType = ErrorType.NOT_FOUND,
                    errorCode = ErrorCode.NO_BIZ_CONNECTION
                )
            }

            bizConnection.applyStatus = status
            val updatedBizConn = bizConnectionRepository.save(bizConnection)

            val retailer = bizConnection.retailer
            val wholesaler = bizConnection.wholesaler
            val targetPhoneNo = retailer!!.userCredential!!.phoneNo
            val wholesalerName = wholesaler!!.name!!

            when (status) {
                ApplyStatus.APPLYING -> {
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

            return updatedBizConn
        }
    }

    @Transactional
    fun createUser(input: SignUpInput): User {

        val hasUserCred: Boolean = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo).isPresent
        if (hasUserCred) {
            val msg = SAME_PHONE_NO_ERR
            throw CustomException(
                message = msg,
                errorType = ErrorType.INTERNAL,
                errorCode = ErrorCode.PHONE_NO_DUPLICATED
            )
        }

        val belongsTo = when (AccountRole.valueOf(input.role.toString())) {
            AccountRole.RETAILER -> null
            AccountRole.WHOLESALER_EMPR -> DEFAULT_WHOLESALER_BELONGS_TO
            AccountRole.WHOLESALER_EMPE -> DEFAULT_WHOLESALER_BELONGS_TO
        }

        val user = User.builder(
            name = input.name,
            belongsTo = belongsTo,
        )
        val savedUser = userRepository.save(user)

        val userCred = UserCredential.create(
            user = savedUser,
            phoneNo = input.phoneNo,
            password = passwordEncoder.encode(input.password),
            role = AccountRole.valueOf(input.role.name),
        )
        userCredentialRepository.save(userCred)
        return savedUser
    }

    @Transactional
    fun signOut(userId: Long) {

        val authToken = authTokenRepository.findByUserId(userId).orElseThrow {
            val msg = "There's no user to sign out."
            CustomException(
                message = msg,
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.NO_USER
            )
        }
        authTokenRepository.deleteById(authToken.id!!)
    }

    @Transactional
    fun getConnectedEmployerId(employeeId: Long): Long {

            val connections = wholesalerConnectionRepository.findAll(WholesalerConnSpecs.byEmployeeId(employeeId))
            if (connections.isEmpty()) throw CustomException(
                message = "There's no connection data between employer and employees",
                errorType = ErrorType.NOT_FOUND,
                errorCode = ErrorCode.NO_WHOLESALE_CONNECTION
            )
            val employer = connections[0].employer!!
            return employer.id!!
        }
    }

    private fun validateUrl(urlString: String?): Boolean {
        return try {
            URL(urlString)
            true
        } catch (ignored: Exception) {
            false
        }
    }

    private fun validatePassword(password: String): Boolean {
        // 영문, 숫자, 특수문자 조합 + 공백없음 + 8자 이상
        val minLength = 8
        val passwordPattern = "^(?!.* )(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#\$%^&*]).{$minLength,}\$".toRegex()
        return password.matches(passwordPattern)
    }
}