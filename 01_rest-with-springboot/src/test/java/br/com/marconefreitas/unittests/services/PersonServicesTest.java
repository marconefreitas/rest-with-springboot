package br.com.marconefreitas.unittests.services;

import br.com.marconefreitas.data.dto.PersonDTO;
import br.com.marconefreitas.exception.RequiredObjectNullException;
import br.com.marconefreitas.model.Person;
import br.com.marconefreitas.repository.PersonRepository;
import br.com.marconefreitas.services.PersonServices;
import br.com.marconefreitas.unittests.mapper.MockPerson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {

    MockPerson input;

    @Mock
    PersonRepository repository;

    @InjectMocks
    private PersonServices services;

    @BeforeEach
    void setUp() {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Person person = input.mockEntity(1);
        person.setId(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(person));

        var result = services.findById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getLinks());

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/v1/person")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("DELETE")
                ));
        Assertions.assertEquals("Address Test1", result.getAddress());
    }


    @Test
    void create() {
        Person person = input.mockEntity(1);

        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        Mockito.when(repository.save(person)).thenReturn(persisted);

        var result = services.create(dto);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getLinks());

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/v1/person")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("DELETE")
                ));
    }

    @Test
    void testCreateNullException(){
        Exception ex = Assertions.assertThrows(RequiredObjectNullException.class,
                () ->{ services.create(null);
        });

        String expectedMsg = "It is not allowed persist a null object";
        String actualMsg = ex.getMessage();
        Assertions.assertTrue(actualMsg.contains(expectedMsg));

    }

    @Test
    void update() {
        Person person = input.mockEntity(1);

        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(person));
        Mockito.when(repository.save(person)).thenReturn(persisted);
        var result = services.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getLinks());

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/v1/person")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("DELETE")
                ));
    }

    @Test
    void delete() {
        Person person = input.mockEntity(1);
        person.setId(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(person));
        services.delete(1L);
        Mockito.verify(repository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(repository, Mockito.times(1)).delete(any(Person.class));
        Mockito.verifyNoMoreInteractions(repository);

    }

    @Test
    @Disabled("Reason : Under development")
    void findAll() {
        List<Person> list = input.mockEntityList();
        Mockito.when(repository.findAll()).thenReturn(list);

        List<PersonDTO> persons = new ArrayList<>(); // services.findAll();
        Assertions.assertNotNull(persons);
        Assertions.assertEquals(14, persons.size());

        var personOne = persons.get(1);


        Assertions.assertNotNull(personOne);
        Assertions.assertNotNull(personOne.getId());
        Assertions.assertNotNull(personOne.getLinks());

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("DELETE")
                ));

    }
}