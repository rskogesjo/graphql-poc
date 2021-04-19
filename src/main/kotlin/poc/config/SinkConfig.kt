package poc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import poc.model.Bet
import reactor.core.publisher.Sinks

@Configuration
open class SinkConfig {
    @Bean
    open fun createSink(): Sinks.Many<Bet> = Sinks.many().replay().all()
}