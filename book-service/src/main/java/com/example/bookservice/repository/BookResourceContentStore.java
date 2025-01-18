package com.example.bookservice.repository;

import com.example.bookservice.model.BookResource;
import org.springframework.content.commons.store.ContentStore;
import org.springframework.stereotype.Component;

@Component
public interface BookResourceContentStore extends ContentStore<BookResource, String> {
}
