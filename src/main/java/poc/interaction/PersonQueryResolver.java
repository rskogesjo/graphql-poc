package poc.interaction;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poc.model.Person;
import poc.service.PersonService;

import java.util.List;
import java.util.Optional;

@Component
public class PersonQueryResolver implements GraphQLQueryResolver {
    private final PersonService service;

    @Autowired
    public PersonQueryResolver(PersonService service) {
        this.service = service;
    }

    public List<Person> getAll() {
        return service.getAllPeople();
    }

    public Optional<Person> getOne(int id) {
        return service.getPersonById(id);
    }
}
