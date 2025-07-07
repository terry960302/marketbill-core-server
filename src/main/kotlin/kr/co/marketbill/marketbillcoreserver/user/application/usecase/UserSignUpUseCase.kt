package kr.co.marketbill.marketbillcoreserver.user.application.usecase

import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.user.application.command.SignUpCommand
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import kr.co.marketbill.marketbillcoreserver.user.application.result.AuthTokenResult
import kr.co.marketbill.marketbillcoreserver.user.application.service.TokenService
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserSignUpUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) {
    fun execute(command: SignUpCommand): AuthTokenResult {
        // 전화번호 중복 검사
        if (userRepository.existsByPhoneNumber(PhoneNumber.from(command.phoneNumber))) {
            throw MarketbillException(ErrorCode.PHONE_NO_DUPLICATED)
        }

        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(command.password)

        // 사용자 생성
        val user = User.create(
            name = command.name,
            phoneNumber = command.phoneNumber,
            password = encodedPassword,
            role = command.role,
            belongsTo = command.belongsTo,
            businessInfo = null,
        )

        // 저장
        val savedUser = userRepository.save(user)

        // 토큰 생성
        return tokenService.generateToken(savedUser)
    }

    fun deleteUser(id: UserId) {
        val user = userRepository.findById(id) ?: throw MarketbillException(ErrorCode.NO_USER)
        val deletedUser = user.softDelete()
        userRepository.save(deletedUser)
    }
}
