package poc.resolver

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import poc.config.CacheConfig
import poc.model.Bet
import reactor.core.publisher.Sinks


@Component
class PersonSubscriptionResolver(
    private val sink: Sinks.Many<Bet>,
    private val cacheConfig: CacheConfig
) : GraphQLSubscriptionResolver {

    fun bet(): Publisher<Bet> {
        return sink.asFlux().filter { cacheConfig.cache.contains(it.accountIndex) }
    }
}
