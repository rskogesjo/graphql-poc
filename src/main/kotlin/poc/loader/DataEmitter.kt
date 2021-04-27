package poc.loader

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import poc.model.Bet
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST
import java.util.*
import javax.annotation.PostConstruct

@Component
@EnableScheduling
class DataEmitter(private val betSink: Sinks.Many<Bet>) {
    private val randomizer = Random(System.currentTimeMillis())

    private val horses = listOf(
        "Robin",
        "Sarah",
        "Varun",
        "Abhi"
    )

    @PostConstruct
    private fun init() {
        betSink.emitNext(
            Bet(
                horse = "Robin",
                stake = randomizer.nextInt()
            ), FAIL_FAST
        )
    }

    @Scheduled(fixedDelay = 1000)
    fun emitBetData() {
        val index = randomizer.nextInt(horses.size)
        val horse = horses[index]

        betSink.emitNext(
            Bet(
                horse = horse,
                stake = randomizer.nextInt()
            ), FAIL_FAST
        )
    }
}