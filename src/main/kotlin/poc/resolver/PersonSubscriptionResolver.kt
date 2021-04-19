package poc.resolver

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import poc.model.Bet
import reactor.core.publisher.Sinks


@Component
class PersonSubscriptionResolver(private val sink: Sinks.Many<Bet>) : GraphQLSubscriptionResolver {
    fun bet(): Publisher<Bet> = sink.asFlux()
}
