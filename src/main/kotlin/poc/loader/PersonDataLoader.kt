package poc.loader

import org.dataloader.DataLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import poc.model.Person

@Component
class PersonDataLoader(@Autowired private val personBatchLoader: PersonBatchLoader) {
    @Bean
    fun dataLoader(): DataLoader<Int, Person> = DataLoader.newDataLoader(personBatchLoader)
}
