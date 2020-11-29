package poc.resolver

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import poc.model.Bet
import reactor.core.publisher.Flux.interval
import java.lang.System.nanoTime
import java.time.Duration.ofSeconds
import kotlin.random.Random

@Component
class PersonSubscriptionResolver : GraphQLSubscriptionResolver {
    fun bet(horse: String): Publisher<Bet> = interval(ofSeconds(1)).map { Bet(horse, Random.nextInt(), nanoTime().toString()) }
}