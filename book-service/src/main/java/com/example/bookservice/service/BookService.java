package com.example.bookservice.service;

import com.example.bookservice.messaging.EventDispatcher;
import com.example.bookservice.messaging.event.NewBookAddedEvent;
import com.example.bookservice.model.Book;
import com.example.bookservice.model.BookCoverImage;
import com.example.bookservice.model.BookResource;
import com.example.bookservice.repository.*;
import com.example.bookservice.service.dto.BookFileDTO;
import com.example.bookservice.service.exception.MissingFileException;
import com.example.bookservice.service.exception.MissingObjectException;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookCoverImageRepository bookCoverImageRepository;
    private final BookCoverImageContentStore bookCoverImageContentStore;
    private final BookResourceRepository bookResourceRepository;
    private final BookResourceContentStore bookResourceContentStore;

    private final EventDispatcher eventDispatcher;
    private final KafkaTemplate<String, Book> kafkaTemplate;

    private static final int STREAM_BUFFER_SIZE = 1024 * 1024;

    @Transactional
    public void addNewBook(final String title, final String author, final String genre) {
        Book book = new Book(title, author, genre);
        bookRepository.save(book);

        BookCoverImage bookCoverImage = new BookCoverImage(book.getBookId());
        bookCoverImageRepository.save(bookCoverImage);

        BookResource bookResource = new BookResource(book.getBookId());
        bookResourceRepository.save(bookResource);
    }


    @Transactional
    public void uploadBookCoverImage(final Long bookId, final MultipartFile coverImage) {
        if (coverImage == null) {
            throw new MissingFileException("Missing cover image file");
        }

        if (!bookRepository.existsByBookId(bookId)) {
            throw new MissingObjectException("Book with given id does not exist");
        }

        Optional<BookCoverImage> bookCoverImageOptional = bookCoverImageRepository.findByBookId(bookId);
        if (bookCoverImageOptional.isEmpty()) {
            throw new MissingObjectException("Book cover image object for this book does not exist");
        }

        try {
            BookCoverImage bookCoverImage = bookCoverImageOptional.get();
            bookCoverImage.setCoverImageMimeType(coverImage.getContentType());
            bookCoverImage.setCoverImageContentLength(coverImage.getSize());
            bookCoverImage.setCoverImageOriginalFileName(coverImage.getOriginalFilename());

            InputStream fileStream = coverImage.getInputStream();
            if (bookCoverImage.getCoverImageContentId() != null) {
                bookCoverImageContentStore.unsetContent(bookCoverImage);
            }
            bookCoverImageContentStore.setContent(bookCoverImage, fileStream);
            bookCoverImageRepository.save(bookCoverImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (checkIfNewBookAddedEvent(bookId)) {
            Book book = bookRepository.findById(bookId).get();
            eventDispatcher.send(new NewBookAddedEvent(
                    "New Book Added", book.getBookId(), book.getTitle(), book.getAuthor(), book.getGenre()
            ));
        }
    }

    @Transactional
    public void uploadBookResource(Long bookId, MultipartFile resource) {
        if (resource == null) {
            throw new MissingFileException("Missing resource file");
        }

        if (!bookRepository.existsByBookId(bookId)) {
            throw new MissingObjectException("Book with given id does not exist");
        }

        Optional<BookResource> bookResourceOptional = bookResourceRepository.findByBookId(bookId);
        if (bookResourceOptional.isEmpty()) {
            throw new MissingObjectException("Book with given id does not exist");
        }

        try {
            BookResource bookResource = bookResourceOptional.get();
            bookResource.setResourceMimeType(resource.getContentType());
            bookResource.setResourceContentLength(resource.getSize());
            bookResource.setResourceOriginalFileName(resource.getOriginalFilename());

            InputStream fileStream = resource.getInputStream();
            if (bookResource.getResourceContentId() != null) {
                bookResourceContentStore.unsetContent(bookResource);
            }
            bookResourceContentStore.setContent(bookResource, fileStream);
            bookResourceRepository.save(bookResource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (checkIfNewBookAddedEvent(bookId)) {
            Book book = bookRepository.findById(bookId).get();
            eventDispatcher.send(new NewBookAddedEvent(
                    "New Book Added", book.getBookId(), book.getTitle(), book.getAuthor(), book.getGenre()
            ));
        }
    }

    private boolean checkIfNewBookAddedEvent(final Long bookId) {
        BookCoverImage bookCoverImage = bookCoverImageRepository.findByBookId(bookId).get();
        BookResource bookResource = bookResourceRepository.findByBookId(bookId).get();
        if (bookCoverImage.getCoverImageContentId() != null & bookResource.getResourceContentId() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        return books;
    }

    @Transactional
    public BookFileDTO getBookCoverImage(Long bookId) {
        Optional<BookCoverImage> bookCoverImageOptional = bookCoverImageRepository.findByBookId(bookId);
        if (bookCoverImageOptional.isEmpty()) {
            throw new MissingObjectException("No book with the specified id");
        }

        BookCoverImage bookCoverImage = bookCoverImageOptional.get();
        if (Objects.isNull(bookCoverImage.getCoverImageContentId()) || "".equals(bookCoverImage.getCoverImageContentId())) {
            throw new MissingFileException("No cover image file for specified book");
        }

        InputStream bookCoverImageInputStream = bookCoverImageContentStore.getContent(bookCoverImage);
        String mimeType = bookCoverImage.getCoverImageMimeType();

        StreamingResponseBody streamingResponseBody = outputStream -> {
            try (BufferedInputStream bufferedInput = new BufferedInputStream(bookCoverImageInputStream, STREAM_BUFFER_SIZE)) {
                BufferedOutputStream bufferedOutput = new BufferedOutputStream(outputStream, STREAM_BUFFER_SIZE);
                bufferedInput.transferTo(bufferedOutput);
                bufferedOutput.flush();
            }
        };

        return new BookFileDTO(mimeType, streamingResponseBody);
    }

    @Transactional
    public BookFileDTO getBookResource(Long bookId) {
        Optional<BookResource> bookResourceOptional = bookResourceRepository.findByBookId(bookId);
        if (bookResourceOptional.isEmpty()) {
            throw new MissingObjectException("No book with the specified id");
        }

        BookResource bookResource = bookResourceOptional.get();
        if (Objects.isNull(bookResource.getResourceContentId()) || "".equals(bookResource.getResourceContentId())) {
            throw new MissingFileException("No resource file for specified book");
        }

        InputStream bookResourceInputStream = bookResourceContentStore.getContent(bookResource);
        String mimeType = bookResource.getResourceMimeType();

        StreamingResponseBody streamingResponseBody = outputStream -> {
            try (BufferedInputStream bufferedInput = new BufferedInputStream(bookResourceInputStream, STREAM_BUFFER_SIZE)) {
                BufferedOutputStream bufferedOutput = new BufferedOutputStream(outputStream, STREAM_BUFFER_SIZE);
                bufferedInput.transferTo(bufferedOutput);
                bufferedOutput.flush();
            }
        };

        Book book = bookRepository.findById(bookId).orElseThrow(MissingObjectException::new);
        kafkaTemplate.send("book", book);

        return new BookFileDTO(mimeType, streamingResponseBody);
    }
}
