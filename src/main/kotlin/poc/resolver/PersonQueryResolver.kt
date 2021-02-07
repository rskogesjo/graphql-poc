package poc.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.dataloader.DataLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.model.Person
import poc.repository.PersonRepository

@Component
class PersonQueryResolver(
    @Autowired private val repository: PersonRepository,
    @Autowired private val personDataLoader: DataLoader<Int, Person>
) : GraphQLQueryResolver {
    fun getAll() = repository.findAll()

    fun getOne(id: Int) = repository.findById(id)

    fun getByDataLoader(ids: List<Int>): List<Person> {
        return ids.map {
            val load = personDataLoader.load(it)
            personDataLoader.dispatch()

            load.get()
        }
    }
}
