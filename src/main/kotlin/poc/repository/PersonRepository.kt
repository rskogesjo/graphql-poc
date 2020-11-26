package poc.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import poc.model.Person

@Repository
interface PersonRepository : JpaRepository<Person, Int>
