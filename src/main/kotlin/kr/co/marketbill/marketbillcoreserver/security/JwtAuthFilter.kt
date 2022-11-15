package kr.co.marketbill.marketbillcoreserver.security

import kr.co.marketbill.marketbillcoreserver.vo.CustomUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class JwtAuthFilter() : GenericFilterBean() {
    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val token: String = jwtProvider.resolveToken((request as HttpServletRequest))
        println("@@ token : $token  @@")
        if (token.isNotBlank()) {
            val isValidToken = jwtProvider.validateToken(token)
            if (isValidToken) {
                val authentication = jwtProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }else{
                // TODO : 유효하지 않으면(엑세스 토큰 만료) => 리프레시 토큰 확인 후 갱신해서 통과시킴
            }


//            val mockUserDetails = CustomUserDetails(email = "tester1@gmail.com", roles = setOf(AccountRoleType.ADMIN))
//            println("@@ authorities : ${mockUserDetails.authorities} @@ ")
//            val mockAuthentication =
//                UsernamePasswordAuthenticationToken(mockUserDetails, "", mockUserDetails.authorities)
//            SecurityContextHolder.getContext().authentication = mockAuthentication
        }
        chain?.doFilter(request, response)
    }


}