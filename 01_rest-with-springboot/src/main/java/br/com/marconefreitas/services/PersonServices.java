package br.com.marconefreitas.services;

import br.com.marconefreitas.controllers.PersonController;
import br.com.marconefreitas.data.dto.PersonDTO;
import br.com.marconefreitas.exception.BadRequestException;
import br.com.marconefreitas.exception.FileStorageException;
import br.com.marconefreitas.exception.RequiredObjectNullException;
import br.com.marconefreitas.exception.ResourceNotFoundException;
import br.com.marconefreitas.file.FileImporter;
import br.com.marconefreitas.file.FileImporterFactory;
import br.com.marconefreitas.file.exporter.FileExporter;
import br.com.marconefreitas.file.exporter.FileExporterFactory;
import br.com.marconefreitas.file.exporter.MediaTypes;
import br.com.marconefreitas.mapper.ObjectMapper;
import br.com.marconefreitas.model.Person;
import br.com.marconefreitas.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;


@Service
public class PersonServices {

    private Logger logger = LoggerFactory.getLogger(Person.class.getName());

    @Autowired
    private PersonRepository repository;

    @Autowired
    private FileImporterFactory fileImporter;

    @Autowired
    private FileExporterFactory fileExporter;


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

        return buildPagedModel(pageable, lista);
    }



    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName,
                                                         Pageable pageable){
        logger.info("Finding person by name");

        Page<Person> lista = repository.findPersonByName(firstName, pageable);

        return buildPagedModel(pageable, lista);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader){
        logger.info("Exporting people page");

        List<PersonDTO> lista = repository.findAll(pageable)
                .map(p -> ObjectMapper.parseObject(p, PersonDTO.class))
                .getContent();
        try {
            FileExporter exporter = this.fileExporter.getExporter(acceptHeader);
            return exporter.exportFile(lista);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export", e);
        }
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

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("importing person from file");

        if (file.isEmpty()) throw new BadRequestException("Invalid file");
        try(InputStream inputStream = file.getInputStream()){
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cant be null"));
            FileImporter importer = this.fileImporter.getImporter(fileName);

            List<Person> entities = importer.importFile(inputStream).stream()
                    .map(dto ->
                        repository.save(ObjectMapper.parseObject(dto, Person.class)))
                    .toList();
            return entities.stream().map(entity -> {
                var dto = ObjectMapper.parseObject(entity, PersonDTO.class);
                addHateOASLink(dto);
                return dto;
            }).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("error processing file");

        }

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

    private PagedModel<EntityModel<PersonDTO>> buildPagedModel(Pageable pageable, Page<Person> lista) {
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
                        .findPersonByName("", 1, 5, "asc"))
                .withRel("findByName")
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
                        .methodOn(PersonController.class))
                .slash("massCreation")
                .withRel("massCreation")
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

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(PersonController.class)
                        .exportPage(1, 5, "asc", null))
                .withRel("exportPage")
                .withType("GET").withTitle("exportPeople"));
    }
}
