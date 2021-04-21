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
    fun bet(authorization: String): Publisher<Bet> {
        val name = cacheConfig.cache["$authorization-Token"]

        return sink.asFlux().filter { it.accountIndex == name }
    }
}
