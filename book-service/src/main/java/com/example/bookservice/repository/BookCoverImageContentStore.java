package com.example.bookservice.repository;

import com.example.bookservice.model.BookCoverImage;
import org.springframework.content.commons.store.ContentStore;
import org.springframework.stereotype.Component;

@Component
public interface BookCoverImageContentStore extends ContentStore<BookCoverImage, String> {
}
