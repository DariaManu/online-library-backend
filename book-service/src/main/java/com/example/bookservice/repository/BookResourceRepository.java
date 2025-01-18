package com.example.bookservice.repository;

import com.example.bookservice.model.BookResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookResourceRepository extends CrudRepository<BookResource, Long> {
    Optional<BookResource> findByBookId(Long bookId);
}
