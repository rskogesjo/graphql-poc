package poc.resolver

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.model.Person
import poc.service.PersonService

@Component
class PersonMutationResolver(@Autowired private val service: PersonService) : GraphQLMutationResolver {
    fun createPerson(name: String, age: Int) = service.savePerson(Person(name = name, age = age))
}
