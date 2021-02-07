package poc.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.dataloader.DataLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.model.Person
import poc.repository.PersonRepository

@Component
class PersonMutationResolver(
    @Autowired private val repository: PersonRepository,
    @Autowired private val personDataLoader: DataLoader<Int, Person>
) : GraphQLMutationResolver {
    fun createPerson(name: String, age: Int) = repository.save(Person(name = name, age = age))
}
