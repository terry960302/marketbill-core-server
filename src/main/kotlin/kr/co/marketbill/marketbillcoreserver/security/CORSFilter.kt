package kr.co.marketbill.marketbillcoreserver.security

import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class CORSFilter : Filter {
    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig?) {
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        (servletResponse as HttpServletResponse).addHeader("Access-Control-Allow-Origin", "*")
        servletResponse.run {
            addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST")
            addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
        }
        if (request.method == "OPTIONS") {
            servletResponse.status = HttpServletResponse.SC_OK
            return
        }
        chain.doFilter(request, servletResponse)
    }

    override fun destroy() {}
}