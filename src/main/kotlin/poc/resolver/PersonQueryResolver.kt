package poc.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.loader.CustomGraphQlContext.Companion.PERSON_DATA_LOADER
import poc.model.Person
import poc.repository.PersonRepository

@Component
class PersonQueryResolver(@Autowired private val repository: PersonRepository) : GraphQLQueryResolver {
    fun getAll() = repository.findAll()

    fun getOne(id: Int) = repository.findById(id)

    fun getByDataLoader(ids: List<Int>, dataFetchingEnvironment: DataFetchingEnvironment): List<Person> {
        val context = dataFetchingEnvironment.getContext<GraphQLContext>()
        val personDataLoader = context.dataLoaderRegistry.get().getDataLoader<Int, Person>(PERSON_DATA_LOADER)

        val promise = personDataLoader.loadMany(ids)
        personDataLoader.dispatch()

        return promise.get()
    }

}
