package poc.resolver

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import poc.model.Bet
import poc.model.Result
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Duration.ofSeconds


@Component
class SubscriptionResolver(private val betSink: Sinks.Many<Bet>) : GraphQLSubscriptionResolver {
    fun bet(authorization: String): Publisher<Bet> = betSink.asFlux().filter { it.horse == authorization }

    fun onNewResult(raceId: String): Publisher<Result> {
        return Flux.range(2, 6)
            .delayElements(ofSeconds(2))
            .map { Result(id = it.toString(), winner = it) }
    }

}
