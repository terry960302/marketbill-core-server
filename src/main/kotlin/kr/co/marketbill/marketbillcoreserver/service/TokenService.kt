package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private val logger: Logger = LoggerFactory.getLogger(TokenService::class.java)
    private val className: String = this.javaClass.simpleName

    fun generateAuthTokenPair(
        userId: Long,
        role: AccountRole,
    ): AuthTokenDto {
        val executedFunc = object : Any() {}.javaClass.enclosingClass.name

        try {
            val accessToken =
                jwtProvider.generateToken(userId, role.toString(), JwtProvider.ACCESS_EXPIRATION)
            val refreshToken =
                jwtProvider.generateToken(userId, role.toString(), JwtProvider.REFRESH_EXPIRATION)
            logger.info("$className.$executedFunc >> completed.")

            return AuthTokenDto(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    @Transactional
    fun upsertAuthToken(userId: Long, newToken: AuthTokenDto): AuthToken {
        val executedFunc = object : Any() {}.javaClass.enclosingClass.name
        try {
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
            logger.info("$className.$executedFunc >> completed.")
            return authTokenRepository.save(authToken)
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }

    fun reissueToken(userId: Long, role: AccountRole): AuthTokenDto {
        val executedFunc = object : Any() {}.javaClass.enclosingClass.name

        try {
            val tokenPair = generateAuthTokenPair(userId, role)
            upsertAuthToken(userId, tokenPair)
            logger.info("$className.$executedFunc >> completed.")
            return tokenPair
        } catch (e: Exception) {
            logger.error("$className.$executedFunc >> ${e.message}.")
            throw e
        }
    }
}