package poc;

import poc.model.Person;
import poc.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository repository;

    @Autowired
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Person savePerson(Person person) {
        return repository.save(person);
    }

    @Transactional(readOnly = true)
    public List<Person> getAllPeople() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> getPersonById(int id) {
        return repository.findById(id);
    }
}
