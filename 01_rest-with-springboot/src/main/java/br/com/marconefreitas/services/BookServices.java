package br.com.marconefreitas.services;

import br.com.marconefreitas.controllers.BookController;
import br.com.marconefreitas.data.dto.BookDTO;
import br.com.marconefreitas.data.dto.PersonDTO;
import br.com.marconefreitas.exception.RequiredObjectNullException;
import br.com.marconefreitas.exception.ResourceNotFoundException;
import br.com.marconefreitas.mapper.ObjectMapper;
import br.com.marconefreitas.model.Book;
import br.com.marconefreitas.repository.BookRepository;
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
public class BookServices {

    private Logger logger = LoggerFactory.getLogger(BookServices.class.getName());

    @Autowired
    private BookRepository repository;

    @Autowired
    private PagedResourcesAssembler<BookDTO> assembler;

    public BookDTO findById(Long id){
        logger.info("Finding one book");
        var entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No record found for this id "+ String.valueOf(id)));
        var dto = ObjectMapper.parseObject(entity, BookDTO.class);
        addHateOASLink(dto);
        return dto;
    }

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable){
        logger.info("Finding all books");

        Page<Book> pages = repository.findAll(pageable);
        var listaHateOAS = pages.map(b ->{
            var dto = ObjectMapper.parseObject(b, BookDTO.class);
            addHateOASLink(dto);
            return dto;
        });

        Link link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                        .methodOn(BookController.class)
                        .findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort())))
                .withSelfRel();


        return assembler.toModel(listaHateOAS, link);
    }

    public BookDTO create(BookDTO book){
        logger.info("Creating one book");
        if(book == null){
            throw new RequiredObjectNullException();
        }

        var entity = ObjectMapper.parseObject(book, Book.class);
        var dto = ObjectMapper.parseObject(repository.save(entity), BookDTO.class);
        addHateOASLink(dto);
        return dto;
    }

    public BookDTO update(BookDTO bookDTO){
        logger.info("Updating one book");
        if(bookDTO == null){
            throw new RequiredObjectNullException();
        }
        Book entity = repository.findById(bookDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this id "+ String.valueOf(bookDTO.getId())));
        entity.setAuthor(bookDTO.getAuthor());
        entity.setPrice(bookDTO.getPrice());
        entity.setLauchDate(bookDTO.getLauchDate());
        entity.setTitle(bookDTO.getTitle());

        var dto = ObjectMapper.parseObject(repository.save(entity), BookDTO.class);
        addHateOASLink(dto);
        return dto;
    }

    public void delete(Long id){
        logger.info("Deleting one book");
        Book b = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found for this id "+ String.valueOf(id)));
        repository.delete(b);

    }

    private void addHateOASLink(BookDTO dto){
        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(BookController.class)
                        .findById(dto.getId()))
                .withSelfRel()
                .withType("GET"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(BookController.class)
                        .delete(dto.getId()))
                .withRel("delete")
                .withType("DELETE"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(BookController.class)
                        .findAll(0, 8, "asc"))
                .withRel("findAll")
                .withType("GET"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(BookController.class)
                        .create(dto))
                .withRel("create")
                .withType("POST"));

        dto.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(BookController.class)
                        .update(dto))
                .withRel("update")
                .withType("PUT"));
    }

}
