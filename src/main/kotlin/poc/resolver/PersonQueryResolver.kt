package poc.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.repository.PersonRepository

@Component
class PersonQueryResolver(@Autowired private val repository: PersonRepository) : GraphQLQueryResolver {
    fun getAll() = repository.findAll()

    fun getOne(id: Int) = repository.findById(id)
}
