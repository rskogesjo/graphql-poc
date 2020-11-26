package poc.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Person(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int = 0, val name: String = "", val age: Int = 0)
