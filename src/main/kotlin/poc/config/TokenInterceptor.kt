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
        if ((request as HttpServletRequest).requestURL.endsWith("/subscriptions")) {
            val accountIndex = request.getHeader(HttpHeaders.AUTHORIZATION)

            if (accountIndex != null) {
                cacheConfig.cache.add(accountIndex)
            }
        }

        chain.doFilter(request, response)
    }
}
