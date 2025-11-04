package br.com.marconefreitas.repository;

import br.com.marconefreitas.integrationtests.AbstractIntegrationTest;
import br.com.marconefreitas.model.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired PersonRepository repository;

    private static Person person;



    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(1)
    void findPersonByName() {
        Pageable page = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPersonByName("Jeroni", page).getContent().get(0);
        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getId());
        Assertions.assertEquals("Jeronimo", person.getFirstName());
    }

    @Test
    @Order(2)
    void disablePerson() {
        Long id = person.getId();

        repository.disablePerson(id);
        var result = repository.findById(id);
        Assertions.assertFalse(result.get().getEnabled());
    }
}