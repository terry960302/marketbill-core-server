package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class AuthService {
    @Autowired
    private lateinit var authTokenRepository: AuthTokenRepository
    @Autowired
    private lateinit var jwtProvider : JwtProvider
    @Autowired
    private lateinit var entityManager : EntityManager

    fun validateRefreshToken(userId: Long): Boolean {
        val token = authTokenRepository.findByUserId(userId)
        if (token.isEmpty) throw CustomException("There's no available auth token.")
        return jwtProvider.validateToken(token.get().refreshToken)
    }

    fun generateAuthToken(
        userId: Long,
        role: AccountRole,
    ): AuthTokenDto {
        val accessToken =
            jwtProvider.generateToken(userId, role.toString(), JwtProvider.accessExpiration)
        val refreshToken =
            jwtProvider.generateToken(userId, role.toString(), JwtProvider.refreshExpiration)

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
}