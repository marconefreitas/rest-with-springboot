package br.com.marconefreitas.unittests.mapper;

import br.com.marconefreitas.data.dto.BookDTO;
import br.com.marconefreitas.model.Book;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockBook {

    public Book mockEntity() {
        return mockEntity(0);
    }

    public BookDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Book> mockEntityList() {
        List<Book> books = new ArrayList<Book>();
        for (int i = 0; i < 14; i++) {
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<BookDTO> mockDTOList() {
        List<BookDTO> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockDTO(i));
        }
        return books;
    }

    public Book mockEntity(Integer number) {
        Book book = new Book();
        book.setAuthor("Author Test" + number);
        book.setLauchDate(new Date());
        book.setId(number.longValue());
        book.setTitle("Title Test" + number);
        book.setPrice(25D);
        return book;
    }

    public BookDTO mockDTO(Integer number) {
        BookDTO book = new BookDTO();
        book.setAuthor("Address Test" + number);
        book.setLauchDate(new Date());
        book.setTitle("Title Test" + number);
        book.setId(number.longValue());
        book.setPrice(25D);
        return book;
    }

}
