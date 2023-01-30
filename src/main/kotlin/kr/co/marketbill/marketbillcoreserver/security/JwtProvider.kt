package kr.co.marketbill.marketbillcoreserver.security

import com.netflix.graphql.types.errors.ErrorType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.graphql.error.InternalErrorException
import kr.co.marketbill.marketbillcoreserver.service.CustomUserDetailsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtProvider(

) {
    @Autowired
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @Value("\${spring.security.jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${spring.security.cookies.httpOnly}")
    private lateinit var isCookieHttpOnly: String

    @Value("\${spring.security.cookies.secure}")
    private lateinit var isCookieSecure: String

    private val logger: Logger = LoggerFactory.getLogger(JwtProvider::class.java)


    companion object {
        val SIGNATURE_ALG: SignatureAlgorithm = SignatureAlgorithm.HS256
        const val AUTHORIZATION_HEADER_NAME = "Authorization"
        const val ACCESS_TOKEN_COOKIE_NAME = "accessToken"
        const val REFRESH_TOKEN_COOKIE_NAME = "refreshToken"
        const val ACCESS_EXPIRATION: Long =
            3 * 60 * 60 * 1000L // 3hour (response 로 사용될 경우 1000L 곱셈, cookie 의 age 엔 필요없음)
        const val REFRESH_EXPIRATION: Long = 7 * 24 * 60 * 60 * 1000L // 7day
        const val ACCESS_MAX_AGE: Long = (ACCESS_EXPIRATION / 1000L)
        const val REFRESH_MAX_AGE: Long = (REFRESH_EXPIRATION / 1000L)
    }

    fun generateToken(userId: Long, role: String, expiration: Long): String {
        val claims: Claims = Jwts.claims()
        claims["user_id"] = userId
        claims["role"] = role
        return Jwts.builder().setClaims(claims).setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SIGNATURE_ALG, jwtSecret)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        val claims: Claims = getAllClaims(token)
        val exp: Date = claims.expiration
        return exp.after(Date())
    }

    fun parseUserId(token: String): Long {
        val claims: Claims = getAllClaims(token)
        return claims["user_id"].toString().toLong()
    }

    fun parseUserRole(token: String): AccountRole {
        val claims: Claims = getAllClaims(token)
        val roleStr = claims["role"].toString()
        return AccountRole.valueOf(roleStr)
    }

    fun getAuthentication(token: String): Authentication {
        val userId = parseUserId(token)
        val cred = customUserDetailsService.loadUserByUsername("$userId")
        return UsernamePasswordAuthenticationToken(cred, "", cred.authorities)
    }

    fun resolveToken(request: HttpServletRequest): String {
        val tokenWithType = request.getHeader(AUTHORIZATION_HEADER_NAME)
        return filterOnlyToken(tokenWithType)
    }

    fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser().setSigningKey(jwtSecret)
        val jws = parser.parseClaimsJws(token)
        return jws.body
    }

    fun filterOnlyToken(fullToken: String?): String {
        if (fullToken == null) return ""

        val splitStr = fullToken.split(" ")
        return if (splitStr.size == 2) {
            splitStr[1]
        } else {
            ""
        }
    }

    @Deprecated(message = "This function for cookie-based jwt authentication.")
    fun getTokenFromCookie(request: HttpServletRequest, cookieName: String): String {
        val cookies = request.cookies
        val tokens = cookies.filter { it.name == cookieName }
        if (cookies.isEmpty() || tokens.isEmpty()) throw CustomException(
            message = JwtAuthFilter.NO_TOKEN_ERR,
            errorType = ErrorType.UNAUTHENTICATED,
            errorCode = CustomErrorCode.TOKEN_NEEDED
        )
        return tokens[0].value
    }

    @Deprecated(message = "This function for cookie-based jwt authentication.")
    fun setAllTokensToHttpOnlyCookie(response: HttpServletResponse, newToken: AuthTokenDto) {
        setHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE_NAME, newToken.accessToken, ACCESS_MAX_AGE)
        setHttpOnlyCookie(
            response,
            REFRESH_TOKEN_COOKIE_NAME,
            newToken.refreshToken,
            REFRESH_MAX_AGE
        )
    }

    @Deprecated(message = "This function for cookie-based jwt authentication.")
    fun resetAllTokensInHttpOnlyCookie(response: HttpServletResponse) {
        setHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE_NAME, "", 0)
        setHttpOnlyCookie(
            response,
            REFRESH_TOKEN_COOKIE_NAME,
            "",
            0
        )
    }

    /**
     * ## 'Cookie-based Auth' Issue
     *
     * - Safari 브라우저에서 same-site=None 버그 이슈 (same-site=None이 적용이 안됨)
     * - HttpOnly, Secure 모두 true를 해야 보안상 작동하는데, local 로 테스트하기 까다로워짐.(로컬에서 항상 mock https를 가동할 수는 없는 노릇)
     * - Same-site 이슈로 인해 같은 도메인으로 font와 back을 매핑하면 되겠지만 배보다 배꼽이 더 커짐.
     *
     * => 'Authorization Header Token 교환' 방식으로 프론트에서 토큰을 헤러에 담아 주는 방식으로 하기로 함.
     */
    @Deprecated(message = "This function for cookie-based jwt authentication.")
    fun setHttpOnlyCookie(
        response: HttpServletResponse,
        key: String,
        value: String,
        maxAge: Long,
    ) {
        val cookie = ResponseCookie.from(key, value)
            .secure(isCookieSecure.toBoolean())
            .httpOnly(isCookieHttpOnly.toBoolean())
            .sameSite("None")
            .maxAge(maxAge)
            .path("/")
            .build()
        return response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}