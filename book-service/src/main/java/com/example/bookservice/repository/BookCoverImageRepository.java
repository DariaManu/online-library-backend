package com.example.bookservice.repository;

import com.example.bookservice.model.BookCoverImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCoverImageRepository extends CrudRepository<BookCoverImage, Long> {
    public Optional<BookCoverImage> findByBookId(Long bookId);
}
