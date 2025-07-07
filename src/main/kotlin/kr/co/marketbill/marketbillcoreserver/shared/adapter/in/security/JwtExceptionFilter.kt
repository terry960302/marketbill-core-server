package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtExceptionFilter : OncePerRequestFilter() {

    @Throws(IOException::class)
    fun setErrorResponse(status: HttpStatus, res: HttpServletResponse, ex: Throwable) {
        res.status = status.value()
        res.contentType = "application/json; charset=UTF-8"

        val map: MutableMap<String, String> = HashMap()

        map["status"] = res.status.toString()
        map["error"] = status.name
        map["message"] = ex.message.toString()

        val objectMapper = ObjectMapper()
        objectMapper.writeValue(res.outputStream, map)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response) // go to 'JwtAuthenticationFilter'
        } catch (ex: JwtException) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, ex)
        } catch (ex : MarketbillException){
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, ex)
        }
    }
}