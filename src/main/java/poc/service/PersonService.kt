package poc.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import poc.model.Person
import poc.repository.PersonRepository
import java.util.*

@Service
open class PersonService(@Autowired private val repository: PersonRepository) {
    @Transactional(readOnly = true)
    open fun savePerson(person: Person): Person = repository.save(person)

    @Transactional(readOnly = true)
    open fun getAllPeople(): List<Person> = repository.findAll()

    @Transactional(readOnly = true)
    open fun getPersonById(id: Int): Optional<Person> = repository.findById(id)
}
