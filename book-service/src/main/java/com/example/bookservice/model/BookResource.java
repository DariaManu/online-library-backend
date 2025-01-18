package com.example.bookservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.content.commons.annotations.OriginalFileName;

@Entity
@Table(name = "book_resources")
@Getter
@Setter
@NoArgsConstructor
public class BookResource {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "book_resource_id")
    private Long bookResourceId;

    @Column(name = "book_id")
    private Long bookId;

    @ContentId
    @Column(name = "resource_content_id")
    private String resourceContentId;

    @MimeType
    @Column(name = "resource_mime_type")
    private String resourceMimeType;

    @ContentLength
    @Column(name = "resource_content_length")
    private Long resourceContentLength;

    @OriginalFileName
    @Column(name = "resource_original_file_name")
    private String resourceOriginalFileName;

    public BookResource(final Long bookId) {
        this.bookId = bookId;
    }
}
