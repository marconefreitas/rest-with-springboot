package br.com.marconefreitas.repository;

import br.com.marconefreitas.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
