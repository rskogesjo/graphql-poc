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
    private val randomizer = Random(System.currentTimeMillis())
    private val accountIndex = listOf(
        "Robin",
        "Sarah",
        "Varun",
        "Abhi"
    )

    /**
     *  A real emitter would emit data received over some kind of JMS its listening to
     */
    @Scheduled(fixedDelay = 1000)
    fun emitData() {
        val index = randomizer.nextInt(accountIndex.size)
        val horse = accountIndex[index]

        sink.emitNext(
            Bet(
                horse = horse,
                stake = randomizer.nextInt(),
                accountIndex = horse
            ), FAIL_FAST
        )
    }
}