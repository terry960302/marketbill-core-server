package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class TokenService {

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var authTokenRepository: AuthTokenRepository

    fun generateAuthTokenPair(
        userId: Long,
        role: AccountRole,
    ): AuthTokenDto {
        val accessToken =
            jwtProvider.generateToken(userId, role.toString(), JwtProvider.ACCESS_EXPIRATION)
        val refreshToken =
            jwtProvider.generateToken(userId, role.toString(), JwtProvider.REFRESH_EXPIRATION)

        return AuthTokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun upsertAuthToken(userId: Long, newToken: AuthTokenDto): AuthToken {
        val token = authTokenRepository.findByUserId(userId)
        val authToken = if (token.isEmpty) {
            AuthToken(
                user = entityManager.getReference(User::class.java, userId),
                refreshToken = newToken.refreshToken
            )
        } else {
            token.get().refreshToken = newToken.refreshToken
            token.get()
        }
        return authTokenRepository.save(authToken)
    }

    fun reissueToken(userId: Long, role: AccountRole): AuthTokenDto {
        val tokenPair = generateAuthTokenPair(userId, role)
        upsertAuthToken(userId, tokenPair)
        return tokenPair
    }
}