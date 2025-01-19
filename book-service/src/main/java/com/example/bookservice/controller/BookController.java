package com.example.bookservice.controller;

import com.example.bookservice.model.Book;
import com.example.bookservice.service.BookService;
import com.example.bookservice.service.dto.BookFileDTO;
import com.example.bookservice.service.exception.MissingFileException;
import com.example.bookservice.service.exception.MissingObjectException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody CreateNewBookRequest request) {
        bookService.addNewBook(request.getTitle(), request.getAuthor(), request.getGenre());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok().body(bookService.getAllBooks());
    }

    @PostMapping(path = "/{bookId}/coverImage")
    public ResponseEntity<?> addCoverImageToBook(@PathVariable Long bookId,
                                                 @RequestParam("coverImage") MultipartFile coverImage) {
        try {
            bookService.uploadBookCoverImage(bookId, coverImage);
        } catch (MissingFileException | MissingObjectException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/{bookId}/resource")
    public ResponseEntity<?> addResourceToBook(@PathVariable Long bookId,
                                               @RequestParam("resource") MultipartFile resource) {
        try {
            bookService.uploadBookResource(bookId, resource);
        } catch (MissingFileException | MissingObjectException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{bookId}/coverImage")
    public ResponseEntity<StreamingResponseBody> getBookCoverImage(@PathVariable Long bookId) {
        BookFileDTO dto = bookService.getBookCoverImage(bookId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(dto.getMimeType()));

        return ResponseEntity.ok().headers(headers).body(dto.getStreamingResponseBody());
    }

    @GetMapping("/{bookId}/resource")
    public ResponseEntity<StreamingResponseBody> getBookResource(@PathVariable Long bookId) {
        BookFileDTO dto = bookService.getBookResource(bookId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(dto.getMimeType()));

        return ResponseEntity.ok().headers(headers).body(dto.getStreamingResponseBody());
    }
}
