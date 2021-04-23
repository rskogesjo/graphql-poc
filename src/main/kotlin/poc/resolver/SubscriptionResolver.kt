package poc.resolver

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import poc.model.Bet
import poc.model.RaceResult
import reactor.core.publisher.Sinks


@Component
class SubscriptionResolver(
    private val betSink: Sinks.Many<Bet>,
    private val raceSink: Sinks.Many<RaceResult>
) : GraphQLSubscriptionResolver {
    fun bet(authorization: String): Publisher<Bet> = betSink.asFlux().filter { it.horse == authorization }

    fun race(): Publisher<RaceResult> = raceSink.asFlux()
}
