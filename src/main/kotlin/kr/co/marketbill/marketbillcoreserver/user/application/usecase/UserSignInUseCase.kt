package kr.co.marketbill.marketbillcoreserver.user.application.usecase

import kr.co.marketbill.marketbillcoreserver.user.application.command.SignInCommand
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import kr.co.marketbill.marketbillcoreserver.user.application.result.AuthTokenResult
import kr.co.marketbill.marketbillcoreserver.user.application.service.TokenService
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserSignInUseCase(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val tokenService: TokenService
) {
    fun execute(command: SignInCommand): AuthTokenResult {
        // 사용자 조회
        val user =
                userRepository.findByPhoneNumber(PhoneNumber.from(command.phoneNumber))
                        ?: throw MarketbillException(
                                errorCode = ErrorCode.NO_USER
                        )

        // 삭제된 사용자 확인
        if (user.isDeleted()) {
            throw MarketbillException(
                    errorCode = ErrorCode.NO_USER
            )
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(command.password, user.password.value)) {
            throw MarketbillException(
                    errorCode = ErrorCode.INVALID_PASSWORD
            )
        }

        // 토큰 생성
        return tokenService.generateToken(user)
    }
}
