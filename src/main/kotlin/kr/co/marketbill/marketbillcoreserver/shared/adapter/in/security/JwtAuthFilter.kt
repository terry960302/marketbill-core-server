package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    private val logger: Logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    companion object {
        const val NO_TOKEN_ERR = "There's no token to authenticate."
        const val TOKEN_EXPIRED_ERR = "TOKEN EXPIRED"
        const val INVALID_TOKEN_ERR = "INVALID_TOKEN"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = jwtProvider.resolveToken(request)

            if (token.isNotBlank()) {
                jwtProvider.validateToken(token)
                val authentication = jwtProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
            filterChain.doFilter(request, response)
        } catch (ex: ExpiredJwtException) {
            throw JwtException(TOKEN_EXPIRED_ERR)
        } catch (ex: UnsupportedJwtException) {
            throw JwtException(INVALID_TOKEN_ERR)
        } catch (ex: MalformedJwtException) {
            throw JwtException(INVALID_TOKEN_ERR)
        } catch (ex: io.jsonwebtoken.SignatureException) {
            throw JwtException(INVALID_TOKEN_ERR)
        } catch (ex: IllegalArgumentException) {
            throw JwtException(INVALID_TOKEN_ERR)
        }
    }
}