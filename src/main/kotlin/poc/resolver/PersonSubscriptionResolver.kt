package poc.resolver

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import poc.model.Bet
import reactor.core.publisher.Flux
import java.lang.System.nanoTime
import java.time.Duration.ofMillis

@Component
class PersonSubscriptionResolver : GraphQLSubscriptionResolver {
    fun bet(horse: String): Publisher<Bet> = Flux.range(1, 1000)
        .delayElements(ofMillis(50))
        .map { Bet(horse = horse, amount = it, timestamp = nanoTime().toString()) }
}
