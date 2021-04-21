package poc.config

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class TokenInterceptor(private val cacheConfig: CacheConfig) : Filter {
    @Override
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest

        if (request.requestURL.endsWith("/subscriptions")) {
            val token = servletRequest.getHeader(HttpHeaders.AUTHORIZATION)

            if (token != null) {
                cacheConfig.cache[token] = token.replace("-Token", "")
            }
        }

        filterChain.doFilter(servletRequest, servletResponse)
    }
}
