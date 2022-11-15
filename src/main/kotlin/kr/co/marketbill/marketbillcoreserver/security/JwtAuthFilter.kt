package kr.co.marketbill.marketbillcoreserver.security

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
    @Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver? = null
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{
            val token: String = jwtProvider.resolveToken((request as HttpServletRequest))
            if (token.isNotBlank()) {
                val isValidToken = jwtProvider.validateToken(token)
                if (isValidToken) {
                    val authentication = jwtProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                }else{
                    // TODO : 유효하지 않으면(엑세스 토큰 만료) => 리프레시 토큰 확인 후 갱신해서 통과시킴
                }
            }
        }catch (e : Exception){
            log.error("Spring Security Filter Chain Exception:", e);
            resolver?.resolveException(request, response, null, e);
        }
        filterChain.doFilter(request, response)
    }


}