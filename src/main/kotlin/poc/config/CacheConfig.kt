package poc.config

import org.springframework.context.annotation.Configuration

@Configuration
open class CacheConfig {
    open val cache = mutableSetOf<String>()
}
