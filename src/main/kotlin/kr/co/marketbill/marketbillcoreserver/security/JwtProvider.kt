package kr.co.marketbill.marketbillcoreserver.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.service.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest


@Component
class JwtProvider {
    @Autowired
    private lateinit var customUserDetailsService: CustomUserDetailsService


    companion object {
        val SIGNATURE_ALG: SignatureAlgorithm = SignatureAlgorithm.HS256
        private const val jwtSecret = "1234"
        const val accessExpiration = 30 * 60 * 1000L // 30min
        const val refreshExpiration = 7 * 24 * 60 * 60 * 1000L // 7day
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
}