package poc.loader

import org.dataloader.BatchLoader
import org.springframework.stereotype.Component
import poc.model.Person
import poc.repository.PersonRepository
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.CompletionStage

@Component
class AsyncPersonBatchLoader(private val repository: PersonRepository) : BatchLoader<Int, Person> {
    @Override
    override fun load(ids: List<Int>): CompletionStage<List<Person>> = supplyAsync { repository.findAllById(ids) }
}
