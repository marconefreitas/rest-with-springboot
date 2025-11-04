package br.com.marconefreitas.services;

import br.com.marconefreitas.controllers.PersonController;
import br.com.marconefreitas.data.dto.PersonDTO;
import br.com.marconefreitas.exception.RequiredObjectNullException;
import br.com.marconefreitas.exception.ResourceNotFoundException;
import br.com.marconefreitas.mapper.ObjectMapper;
import br.com.marconefreitas.model.Person;
import br.com.marconefreitas.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServices {

    private Logger logger = LoggerFactory.getLogger(Person.class.getName());
    @Autowired
    private PersonRepository repository;

    @Autowired
    private PagedResourcesAssembler<PersonDTO> assembler;



    public PersonDTO findById(Long id){
        logger.info("Finding one person");
        var entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No record found for this id "+ String.valueOf(id)));
        var dto = ObjectMapper.parseObject(entity, PersonDTO.class);
        addHateOASLink( dto);
        return dto;
    }

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable){
        logger.info("Finding all person");

        Page<Person> lista = repository.findAll(pageable);

        var listaHateOAS = lista.map( p -> {
            var dto = ObjectMapper.parseObject(p,  PersonDTO.class);
            addHateOASLink(dto);
            return dto;
        });

        Link findAllLinks = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort())))
                .withSelfRel();
        return assembler.toModel(listaHateOAS, findAllLinks);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName,
                                                         Pageable pageable){
        logger.info("Finding person by name");

        Page<Person> lista = repository.findPersonByName(firstName, pageable);

        var listaHateOAS = lista.map( p -> {
            var dto = ObjectMapper.parseObject(p,  PersonDTO.class);
            addHateOASLink(dto);
            return dto;
        });

        Link findAllLinks = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort())))
                .withSelfRel();
        return assembler.toModel(listaHateOAS, findAllLinks);
    }

    public PersonDTO create(PersonDTO person){
        logger.info("Creating one person");
        if(person == null){
            throw new RequiredObjectNullException();
        }

        var entity = ObjectMapper.parseObject(person, Person.class);
        var dto = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
        addHateOASLink(dto);
        return dto;
    }

    public PersonDTO update(PersonDTO person){
        logger.info("Updating one person");
        if(person == null){
            throw new RequiredObjectNullException();
        }
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this id "+ String.valueOf(person.getId())));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var dto = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
        addHateOASLink(dto);
        return dto;
    }

    public void delete(Long id){
        logger.info("Deleting one person");
        Person p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this id "+ String.valueOf(id)));
        repository.delete(p);

    }

    @Transactional
    public PersonDTO disablePerson(Long id){
        logger.info("Disabling one person");
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this id "+ String.valueOf(id)));
        repository.disablePerson(id);
        var entity = repository.findById(id).get();
        var dto = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
        addHateOASLink(dto);
        return dto;
    }

    private void addHateOASLink( PersonDTO dto) {
        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .findById(dto.getId()))
                .withSelfRel()
                .withType("GET"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .delete(dto.getId()))
                .withRel("delete")
                .withType("DELETE"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .findAll(1, 12, "asc"))
                .withRel("findAll")
                .withType("GET"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .create(dto))
                .withRel("create")
                .withType("POST"));
        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .disablePerson(dto.getId()))
                .withRel("disable")
                .withType("PATCH"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .update(dto))
                .withRel("update")
                .withType("PUT"));
    }
}
