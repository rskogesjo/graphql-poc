package poc.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import poc.service.PersonService

@Component
class PersonQueryResolver(private var service: PersonService) : GraphQLQueryResolver {
    fun getAll() = service.getAllPeople()

    fun getOne(id: Int) = service.getPersonById(id)
}
