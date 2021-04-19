package poc.loader

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import poc.model.Bet
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST
import java.util.*

@Component
@EnableScheduling
class DataEmitter(private val sink: Sinks.Many<Bet>) {

    /**
     *  A real emitter would emit data received over some kind of JMS its listening to
     */
    @Scheduled(fixedDelay = 3000)
    fun emitData() {
        sink.emitNext(
            Bet(
                horse = UUID.randomUUID().toString(),
                stake = 100
            ), FAIL_FAST
        )
    }
}