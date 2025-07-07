package kr.co.marketbill.marketbillcoreserver.user.application.service

import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import java.util.*
import kr.co.marketbill.marketbillcoreserver.user.application.result.AuthTokenResult
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.stereotype.Service

@Service
class TokenService {

    fun generateToken(user: User): AuthTokenResult {
        // TODO: 실제 JWT 토큰 생성 로직 구현 필요
        val accessToken = "access_token_${user.id?.value ?: 0}_${System.currentTimeMillis()}"
        val refreshToken = "refresh_token_${user.id?.value ?: 0}_${System.currentTimeMillis()}"

        return AuthTokenResult.from(accessToken, refreshToken)
    }

    fun reissueToken(userId: UserId, role: AccountRole): AuthTokenResult {
        // TODO: 실제 JWT 토큰 재발급 로직 구현 필요
        val accessToken = "access_token_${userId.value}_${System.currentTimeMillis()}"
        val refreshToken = "refresh_token_${userId.value}_${System.currentTimeMillis()}"

        return AuthTokenResult.from(accessToken, refreshToken)
    }

    fun parseUserId(token: String): UserId {
        // TODO: 실제 JWT 토큰에서 userId 추출 로직 구현 필요
        return UserId.from(1L) // 임시 구현
    }

    fun parseUserRole(token: String): AccountRole {
        // TODO: 실제 JWT 토큰에서 role 추출 로직 구현 필요
        return AccountRole.RETAILER // 임시 구현
    }
}
