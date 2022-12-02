package kr.co.marketbill.marketbillcoreserver.security

import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.UserRepository
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthFilter : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var userService: UserService

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
            val token: String = jwtProvider.resolveToken((request as HttpServletRequest))
            if (token.isNotBlank()) {
                val isValidToken = jwtProvider.validateToken(token)
                if (isValidToken) {
                    val authentication = jwtProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    processInvalidCase(token)
                }
            }
        } catch (e: Exception) {
            log.error("Spring Security Filter Chain Exception:", e);
            resolver?.resolveException(request, response, null, e);
        }
        filterChain.doFilter(request, response)
    }


    fun processInvalidCase(token: String): Unit {
        val userId = jwtProvider.parseUserId(token)
        val role = jwtProvider.parseUserRole(token)
        val isValidRefreshToken = userService.validateRefreshToken(userId)
        if (isValidRefreshToken) {
            val newToken: AuthTokenDto = userService.generateAuthToken(userId, role)
            userService.upsertAuthToken(userId, newToken)
            val authentication = jwtProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        } else {
            throw CustomException("All tokens are not valid.")
        }
    }


}