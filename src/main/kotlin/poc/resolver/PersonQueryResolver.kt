package poc.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.dataloader.DataLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.loader.CustomGraphQlContext.Companion.PERSON_DATA_LOADER
import poc.model.Person
import poc.repository.PersonRepository

@Component
class PersonQueryResolver(@Autowired private val repository: PersonRepository) : GraphQLQueryResolver {
    fun getAll() = repository.findAll()

    fun getOne(id: Int) = repository.findById(id)

    fun getByDataLoader(ids: List<Int>, environment: DataFetchingEnvironment): List<Person> {
        val dataLoader = personDataLoader(environment)

        val promise = dataLoader.loadMany(ids)
        dataLoader.dispatch()

        return promise.get()
    }

    private fun personDataLoader(environment: DataFetchingEnvironment): DataLoader<Int, Person> {
        val context = environment.getContext<GraphQLContext>()

        return context.dataLoaderRegistry
            .orElseThrow { IllegalStateException("No data loader registry!") }
            .getDataLoader(PERSON_DATA_LOADER)
    }

}
