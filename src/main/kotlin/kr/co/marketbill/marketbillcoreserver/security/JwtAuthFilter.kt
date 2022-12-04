package kr.co.marketbill.marketbillcoreserver.security

import io.jsonwebtoken.JwtException
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthFilter : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private lateinit var resolver: HandlerExceptionResolver

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    companion object {
        const val NO_TOKEN_ERR = "There's no token to authenticate."
        const val TOKEN_EXPIRED_ERR = "TOKEN EXPIRED"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {


        val token = jwtProvider.resolveToken(request)

        if (token.isNotBlank()) {
            val isValidToken = jwtProvider.validateToken(token)

            if (isValidToken) {
                val authentication = jwtProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            } else {
                throw JwtException(TOKEN_EXPIRED_ERR)
            }
        }
        filterChain.doFilter(request, response)
    }


    @Deprecated(message = "It's used for cookie jwt method")
    fun processCookieToken(request: HttpServletRequest, response: HttpServletResponse) {
        val hasTokens = existsToken(request)

        if (hasTokens) {
            val accessToken = jwtProvider.getTokenFromCookie(request, JwtProvider.ACCESS_TOKEN_COOKIE_NAME)
            val isValidAccessToken = jwtProvider.validateToken(accessToken)
            if (isValidAccessToken) {
                val authentication = jwtProvider.getAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = authentication
            } else {
                val refreshToken = jwtProvider.getTokenFromCookie(request, JwtProvider.REFRESH_TOKEN_COOKIE_NAME)
                val isValidRefreshToken = jwtProvider.validateToken(refreshToken)

                if (isValidRefreshToken) {
                    val newAccessToken = reissueAccessToken(accessToken, response)
                    val authentication = jwtProvider.getAuthentication(newAccessToken)
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    jwtProvider.resetAllTokensInHttpOnlyCookie(response)
                    throw CustomException("All tokens are expired.")
                }
            }
        }
    }

    @Deprecated(message = "It's used for cookie jwt method")
    fun existsToken(request: HttpServletRequest): Boolean {
        val cookies = request.cookies
        val tokens =
            cookies.filter { it.name == JwtProvider.ACCESS_TOKEN_COOKIE_NAME || it.name == JwtProvider.REFRESH_TOKEN_COOKIE_NAME }
        return tokens.isNotEmpty()
    }

    @Deprecated(message = "It's used for cookie jwt method")
    fun reissueAccessToken(accessToken: String, response: HttpServletResponse): String {
        val userId = jwtProvider.parseUserId(accessToken)
        val role = jwtProvider.parseUserRole(accessToken)
        val newAccessToken =
            jwtProvider.generateToken(userId, role.toString(), JwtProvider.ACCESS_EXPIRATION)
        jwtProvider.setHttpOnlyCookie(
            response,
            JwtProvider.ACCESS_TOKEN_COOKIE_NAME,
            newAccessToken,
            JwtProvider.ACCESS_MAX_AGE
        )
        return newAccessToken
    }
}