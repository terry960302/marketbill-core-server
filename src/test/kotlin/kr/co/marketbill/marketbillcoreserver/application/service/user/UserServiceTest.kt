package kr.co.marketbill.marketbillcoreserver.application.service.user

import java.util.*
import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.application.dto.response.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.application.dto.response.MessageResponseDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.UserCredential
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.*
import kr.co.marketbill.marketbillcoreserver.application.service.common.MessagingService
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.types.SignInInput
import kr.co.marketbill.marketbillcoreserver.types.SignUpInput
import kr.co.marketbill.marketbillcoreserver.types.UpdatePasswordInput
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.EntityManager

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock lateinit var entityManager: EntityManager
    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var userCredentialRepository: UserCredentialRepository
    @Mock lateinit var bizConnectionRepository: BizConnectionRepository
    @Mock lateinit var wholesalerConnectionRepository: WholesalerConnectionRepository
    @Mock lateinit var passwordEncoder: BCryptPasswordEncoder
    @Mock lateinit var messagingService: MessagingService
    @Mock lateinit var authTokenRepository: AuthTokenRepository
    @Mock lateinit var businessInfoRepository: BusinessInfoRepository
    @Mock lateinit var tokenService: TokenService

    @InjectMocks lateinit var userService: UserService

    private lateinit var retailer: User
    private lateinit var wholesaler: User
    private lateinit var retailerCredential: UserCredential
    private lateinit var wholesalerCredential: UserCredential

    @BeforeEach
    fun setUp() {
        retailer = TestFixtures.user(id = 1L, name = "retailer")
        retailerCredential = TestFixtures.credential(id = 1L, user = retailer, phone = "01011112222", role = AccountRole.RETAILER)
        retailer = TestFixtures.user(id = 1L, name = "retailer", credential = retailerCredential)

        wholesaler = TestFixtures.user(id = 2L, name = "wholesaler", belongsTo = "양재")
        wholesalerCredential = TestFixtures.credential(id = 2L, user = wholesaler, phone = "01099998888", role = AccountRole.WHOLESALER_EMPR)
        wholesaler = TestFixtures.user(id = 2L, name = "wholesaler", belongsTo = "양재", credential = wholesalerCredential)
    }

    @Test
    fun `회원 가입 성공`() {
        // given
        val input = SignUpInput(name = "retailer", phoneNo = "01011112222", password = "test123@", role = kr.co.marketbill.marketbillcoreserver.types.AccountRole.RETAILER)
        `when`(userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo)).thenReturn(Optional.empty())
        `when`(passwordEncoder.encode(input.password)).thenReturn("hashed")
        `when`(userRepository.save(any<User>())).thenReturn(retailer)
        `when`(userCredentialRepository.save(any<UserCredential>())).thenReturn(retailerCredential)
        val token = AuthTokenDto("access", "refresh")
        `when`(tokenService.generateAuthTokenPair(anyLong(), any())).thenReturn(token)
        `when`(tokenService.upsertAuthToken(anyLong(), any())).thenReturn(AuthToken(id = 1L, refreshToken = "refresh", user = retailer))

        // when
        val result = userService.signUp(input)

        // then
        assert(result.accessToken == "access")
        verify(userRepository).save(any<User>())
        verify(userCredentialRepository).save(any<UserCredential>())
        verify(tokenService).generateAuthTokenPair(retailer.id!!, AccountRole.RETAILER)
    }

    @Test
    fun `로그인 성공`() {
        // given
        val input = SignInInput(phoneNo = "01011112222", password = "test123@")
        `when`(userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo)).thenReturn(Optional.of(retailerCredential))
        `when`(passwordEncoder.matches(input.password, retailerCredential.password)).thenReturn(true)
        val token = AuthTokenDto("access", "refresh")
        `when`(tokenService.generateAuthTokenPair(anyLong(), any())).thenReturn(token)
        `when`(tokenService.upsertAuthToken(anyLong(), any())).thenReturn(AuthToken(id = 1L, refreshToken = "refresh", user = retailer))

        // when
        val result = userService.signIn(input)

        // then
        assert(result.refreshToken == "refresh")
        verify(tokenService).generateAuthTokenPair(retailer.id!!, AccountRole.RETAILER)
    }

    @Test
    fun `비밀번호 변경 성공`() {
        // given
        val input = UpdatePasswordInput(userId = 1, phoneNo = "01011112222", password = "new123@A")
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(retailer))
        `when`(passwordEncoder.encode(input.password)).thenReturn("hashed2")
        `when`(userCredentialRepository.save(any<UserCredential>())).thenReturn(retailerCredential)

        // when
        userService.updatePassword(input)

        // then
        verify(userCredentialRepository).save(any<UserCredential>())
    }

    @Test
    fun `비즈니스 연결 생성 성공`() {
        // given
        `when`(bizConnectionRepository.findAll(any())).thenReturn(listOf())
        `when`(entityManager.getReference(User::class.java, 1L)).thenReturn(retailer)
        `when`(entityManager.getReference(User::class.java, 2L)).thenReturn(wholesaler)
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(retailer))
        `when`(userRepository.findById(2L)).thenReturn(Optional.of(wholesaler))
        runBlocking {
            `when`(messagingService.sendApplyBizConnectionSMS(anyString(), anyString())).thenReturn(
                MessageResponseDto("1", "time", "200", "OK")
            )
        }
        val connection = BizConnection(id = 1L, retailer = retailer, wholesaler = wholesaler, applyStatus = ApplyStatus.APPLYING)
        `when`(bizConnectionRepository.save(any<BizConnection>())).thenReturn(connection)

        // when
        val result = userService.createBizConnection(1L, 2L)

        // then
        assert(result.retailer == retailer)
        verify(bizConnectionRepository).save(any<BizConnection>())
    }
}
