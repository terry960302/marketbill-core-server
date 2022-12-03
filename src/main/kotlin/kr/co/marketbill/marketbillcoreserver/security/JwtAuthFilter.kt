package kr.co.marketbill.marketbillcoreserver.security

import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthFilter : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    companion object {
        const val NO_TOKEN_ERR = "There's no token to authenticate."
    }

    /**
     *
     * ## Access Control Steps
     *
     * 1. 엑세스 토큰 만료(유효x) -> userId에 해당하는 저장된 refreshToken값 확인
     * 2. refreshToken 도 유효한지 확인
     *
     *      a. 유효 -> 새로운 토큰을 발급하고 디비에 refreshToken을 새로 저장 후 새로발급된 토큰은 반환(클라에서 저장하게 함)
     *
     *        - 저장 후엔 로직은 그대로 진행되도록 에러 반환x (authentication 셋업 진행)
     *
     *      b. 유효x -> 에러 반환
     *
     * => refreshToken 의 시간이 지난 경우에만 서비스 접근이 제한되는 구조.(자주 쓰는 사람은 재로그인 필요가 거의 없고, 자주 안쓰면 로그아웃됨)
     */
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver? = null
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
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
        } catch (e: Exception) {
            log.error("Spring Security Filter Chain Exception:", e);
            resolver?.resolveException(request, response, null, e);
        }
        filterChain.doFilter(request, response)
    }

    fun existsToken(request: HttpServletRequest): Boolean {
        val cookies = request.cookies
        val tokens =
            cookies.filter { it.name == JwtProvider.ACCESS_TOKEN_COOKIE_NAME || it.name == JwtProvider.REFRESH_TOKEN_COOKIE_NAME }
        return tokens.isNotEmpty()
    }

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