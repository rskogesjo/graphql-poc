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
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpServletRequest = request as HttpServletRequest

        if (httpServletRequest.requestURL.endsWith("/subscriptions")) {
            val token = request.getHeader(HttpHeaders.AUTHORIZATION)

            if (token != null) {
                cacheConfig.cache[token] = token.replace("-Token", "")
            }
        }

        chain.doFilter(request, response)
    }
}
