package br.com.marconefreitas.unittests.services;

import br.com.marconefreitas.data.dto.BookDTO;
import br.com.marconefreitas.exception.RequiredObjectNullException;
import br.com.marconefreitas.model.Book;
import br.com.marconefreitas.repository.BookRepository;
import br.com.marconefreitas.services.BookServices;
import br.com.marconefreitas.unittests.mapper.MockBook;
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
class BookServicesTest {

    MockBook input;

    @Mock
    BookRepository repository;

    @InjectMocks
    private BookServices services;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Book book = input.mockEntity(1);
        book.setId(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(book));

        var result = services.findById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getLinks());

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/v1/book")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("DELETE")
                ));
        Assertions.assertEquals("Author Test1", result.getAuthor());
    }


    @Test
    void create() {
        Book persisted = input.mockEntity(1);;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        Mockito.when(repository.save(any(Book.class))).thenReturn(persisted);

        var result = services.create(dto);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getLinks());

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/v1/book")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/1")
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
        Book book = input.mockEntity(1);

        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(repository.save(book)).thenReturn(persisted);
        var result = services.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getLinks());

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/v1/book")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("DELETE")
                ));
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        book.setId(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(book));
        services.delete(1L);
        Mockito.verify(repository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(repository, Mockito.times(1)).delete(any(Book.class));
        Mockito.verifyNoMoreInteractions(repository);

    }

    @Test
    @Disabled
    void findAll() {
        List<Book> list = input.mockEntityList();
        Mockito.when(repository.findAll()).thenReturn(list);

        List<BookDTO> books = new ArrayList<>(); // services.findAll();
        Assertions.assertNotNull(books);
        Assertions.assertEquals(14, books.size());

        var personOne = books.get(1);


        Assertions.assertNotNull(personOne);
        Assertions.assertNotNull(personOne.getId());
        Assertions.assertNotNull(personOne.getLinks());

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("GET")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("POST")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book")
                        && link.getType().equals("PUT")
                ));

        Assertions.assertTrue(personOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("DELETE")
                ));

    }
}