package kr.co.marketbill.marketbillcoreserver.security

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.service.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.context.request.ServletWebRequest
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtProvider(

) {
    @Autowired
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @Value("\${spring.security.jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${spring.security.cookies.httpOnly}')}")
    private lateinit var isCookieHttpOnly: String

    @Value("\${spring.security.cookies.secure}')}")
    private lateinit var isCookieSecure: String


    companion object {
        val SIGNATURE_ALG: SignatureAlgorithm = SignatureAlgorithm.HS256
        const val ACCESS_TOKEN_COOKIE_NAME = "access_token"
        const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
        const val ACCESS_EXPIRATION: Long = 30 * 60 * 1000L // 30min (response로 사용될 경우 1000L 곱셈, cookie의 age엔 필요없음)
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
        val tokenWithType = request.getHeader("Authorization")
        return filterOnlyToken(tokenWithType)
    }

    fun getAllClaims(token: String): Claims {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body
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

    fun getTokenFromCookie(request: HttpServletRequest, cookieName: String): String {
        val cookies = request.cookies
        val tokens = cookies.filter { it.name == cookieName }
        if (cookies.isEmpty() || tokens.isEmpty()) throw CustomException(JwtAuthFilter.NO_TOKEN_ERR)
        return tokens[0].value
    }

    fun setAllTokensToHttpOnlyCookie(response: HttpServletResponse, newToken: AuthTokenDto) {
        setHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE_NAME, newToken.accessToken, ACCESS_MAX_AGE)
        setHttpOnlyCookie(
            response,
            REFRESH_TOKEN_COOKIE_NAME,
            newToken.refreshToken,
            REFRESH_MAX_AGE
        )
    }

    fun resetAllTokensInHttpOnlyCookie(response: HttpServletResponse) {
        setHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE_NAME, "", ACCESS_MAX_AGE)
        setHttpOnlyCookie(
            response,
            REFRESH_TOKEN_COOKIE_NAME,
            "",
            REFRESH_MAX_AGE
        )
    }

    fun setHttpOnlyCookie(
        response: HttpServletResponse,
        key: String,
        value: String,
        maxAge: Long,
    ) {
        val cookie = ResponseCookie.from(key, value)
            .secure(true)
            .httpOnly(true)
            .sameSite("None")
            .maxAge(maxAge)
            .path("/")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        return
    }
}