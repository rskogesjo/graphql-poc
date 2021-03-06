package poc.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.dataloader.DataLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.config.CustomGraphQlContext.Companion.PERSON_DATA_LOADER
import poc.model.Person
import poc.model.RaceResult
import poc.model.Result
import poc.repository.PersonRepository

@Component
class QueryResolver(@Autowired private val repository: PersonRepository) : GraphQLQueryResolver {
    fun getAll() = repository.findAll()

    fun getOne(id: Int) = repository.findById(id)

    fun getByDataLoader(ids: List<Int>, environment: DataFetchingEnvironment): List<Person> {
        val dataLoader = personDataLoader(environment)

        val promise = dataLoader.loadMany(ids)
        dataLoader.dispatch()

        return promise.get()
    }

    fun getRaceResult(id: String) = RaceResult(id = id, races = listOf(Result(id = 1.toString(), winner = 1)))

    private fun personDataLoader(environment: DataFetchingEnvironment): DataLoader<Int, Person> {
        val context = environment.getContext<GraphQLContext>()

        return context.dataLoaderRegistry
            .orElseThrow { IllegalStateException("No data loader registry!") }
            .getDataLoader(PERSON_DATA_LOADER)
    }

}
