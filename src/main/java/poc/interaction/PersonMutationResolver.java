package poc.interaction;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poc.model.Person;
import poc.service.PersonService;

@Component
public class PersonMutationResolver implements GraphQLMutationResolver {
    private final PersonService service;

    @Autowired
    public PersonMutationResolver(PersonService service) {
        this.service = service;
    }

    public Person createPerson(String name, int age) {
        return service.savePerson(new Person(name, age));
    }
}
