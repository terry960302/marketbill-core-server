package kr.co.marketbill.marketbillcoreserver.legacy.application.service.user

import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.security.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    ): kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.AuthTokenDto {
        val accessToken =
            jwtProvider.generateToken(userId, role.toString(), JwtProvider.ACCESS_EXPIRATION)
        val refreshToken =
            jwtProvider.generateToken(userId, role.toString(), JwtProvider.REFRESH_EXPIRATION)

        return kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.AuthTokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @Transactional
    fun upsertAuthToken(userId: Long, newToken: kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.AuthTokenDto): AuthToken {
        val authToken = authTokenRepository.findByUserId(userId)
            .map {
                it.updateRefreshToken(newToken.refreshToken)
                it
            }.orElseGet {
                AuthToken.create(
                    user = entityManager.getReference(User::class.java, userId),
                    refreshToken = newToken.refreshToken,
                )
            }
        return authTokenRepository.save(authToken)
    }

    fun reissueToken(userId: Long, role: AccountRole): kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response.AuthTokenDto {
        val tokenPair = generateAuthTokenPair(userId, role)
        upsertAuthToken(userId, tokenPair)
        return tokenPair
    }
}
