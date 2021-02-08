package poc.loader

import org.dataloader.BatchLoader
import org.springframework.stereotype.Component
import poc.model.Person
import poc.repository.PersonRepository
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@Component
class AsyncPersonBatchLoader(private val repository: PersonRepository) : BatchLoader<Int, Person> {
    override fun load(ids: List<Int>): CompletionStage<List<Person>> {
        return CompletableFuture.supplyAsync { getAllPersons(ids) }
    }

    private fun getAllPersons(ids: List<Int>): List<Person> {
        return ids.map { repository.findById(it).get() }
    }
}
