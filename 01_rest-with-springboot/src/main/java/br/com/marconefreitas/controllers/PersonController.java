package br.com.marconefreitas.controllers;


import br.com.marconefreitas.controllers.docs.PersonControllerDocs;
import br.com.marconefreitas.data.dto.PersonDTO;
import br.com.marconefreitas.services.PersonServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "htpp://localhost:8080")
@Tag(name = "Person",description = "Endpoints for people")
@RestController
@RequestMapping("/api/v1/person")
public class PersonController implements PersonControllerDocs {

    @Autowired
    private PersonServices services;

    @Override
    @CrossOrigin(origins = "htpp://localhost:8080")
    @GetMapping(value = "/{id}", produces = {       MediaType.APPLICATION_JSON_VALUE,
                                                    MediaType.APPLICATION_XML_VALUE,
                                                    MediaType.APPLICATION_YAML_VALUE
    })
    public PersonDTO findById(@PathVariable("id") Long id){
        var person  = services.findById(id);
        return person;
    }



    @Override
    @GetMapping(produces = {    MediaType.APPLICATION_JSON_VALUE,
                                MediaType.APPLICATION_XML_VALUE,
                                MediaType.APPLICATION_YAML_VALUE
    })
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction){

        Direction sortDirection = "asc".equalsIgnoreCase(direction)? Direction.ASC: Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(services.findAll(pageable));
    }

    @Override
    @GetMapping(value = "/findByName/{firstName}",
            produces = {    MediaType.APPLICATION_JSON_VALUE,
                                MediaType.APPLICATION_XML_VALUE,
                                MediaType.APPLICATION_YAML_VALUE
    })
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findPersonByName(
            @PathVariable("firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction){

        Direction sortDirection = "asc".equalsIgnoreCase(direction)? Direction.ASC: Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(services.findByName(firstName, pageable));
    }


    @Override
   // @CrossOrigin(origins = {"htpp://localhost:8080"})
    @PostMapping(
            consumes = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_YAML_VALUE
            },
            produces = {    MediaType.APPLICATION_JSON_VALUE,
                            MediaType.APPLICATION_XML_VALUE,
                            MediaType.APPLICATION_YAML_VALUE
            })
    public PersonDTO create(@RequestBody PersonDTO person){
        return services.create(person);
    }


    @Override
    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {    MediaType.APPLICATION_JSON_VALUE,
                            MediaType.APPLICATION_XML_VALUE,
                            MediaType.APPLICATION_YAML_VALUE
            })
    public PersonDTO update(@RequestBody PersonDTO person){
        return services.update(person);
    }


    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        services.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping( value = "/{id}",
                    produces = {    MediaType.APPLICATION_JSON_VALUE,
                                    MediaType.APPLICATION_XML_VALUE,
                                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTO disablePerson(@PathVariable("id") Long id) {
        return services.disablePerson(id);
    }
}
