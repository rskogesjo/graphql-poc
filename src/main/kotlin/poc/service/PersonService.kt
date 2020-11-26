package poc.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import poc.model.Person
import poc.repository.PersonRepository
import java.util.*

@Service
class PersonService(@Autowired private val repository: PersonRepository) {
    fun savePerson(person: Person): Person = repository.save(person)

    fun getAllPeople(): List<Person> = repository.findAll()

    fun getPersonById(id: Int): Optional<Person> = repository.findById(id)
}
