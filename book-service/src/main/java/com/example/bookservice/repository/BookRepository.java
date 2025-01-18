package com.example.bookservice.repository;

import com.example.bookservice.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    public Boolean existsByBookId(Long bookId);
}
