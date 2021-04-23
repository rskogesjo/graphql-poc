package poc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import poc.model.Bet
import poc.model.RaceResult
import reactor.core.publisher.Sinks

@Configuration
open class SinkConfig {
    @Bean
    open fun createBetData(): Sinks.Many<Bet> = Sinks.many().replay().all()

    @Bean
    open fun createRacingData(): Sinks.Many<RaceResult> = Sinks.many().replay().all()
}