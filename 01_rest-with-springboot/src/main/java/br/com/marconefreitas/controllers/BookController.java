package br.com.marconefreitas.controllers;

import br.com.marconefreitas.controllers.docs.BookControllerDocs;
import br.com.marconefreitas.data.dto.BookDTO;
import br.com.marconefreitas.services.BookServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book", description = "Endpoints for Books")
@RestController
@RequestMapping("/api/v1/book")
public class BookController implements BookControllerDocs {

    @Autowired
    private BookServices services;


    @Override
    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
    })
    public BookDTO findById(@PathVariable("id") Long id){
        var book  = services.findById(id);
        return book;
    }



    @Override
    @GetMapping(produces = {    MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
    })
    public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction){
        //return services.findAll();

        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction)? Sort.Direction.ASC: Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "author"));
        return ResponseEntity.ok(services.findAll(pageable));
    }

    @Override
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    public BookDTO create(@RequestBody BookDTO book){
        return services.create(book);
    }

    @Override
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    public BookDTO update(@RequestBody BookDTO book){
        return services.update(book);
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        services.delete(id);
        return ResponseEntity.noContent().build();
    }

}
