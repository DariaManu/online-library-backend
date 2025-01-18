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
@Table(name = "book_cover_images")
@Getter
@Setter
@NoArgsConstructor
public class BookCoverImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "book_cover_image_id")
    private Long bookCoverImageId;

    @Column(name = "book_id")
    private Long bookId;

    @ContentId
    @Column(name = "cover_image_content_id")
    private String coverImageContentId;

    @MimeType
    @Column(name = "cover_image_mime_type")
    private String coverImageMimeType;

    @ContentLength
    @Column(name = "cover_image_content_length")
    private Long coverImageContentLength;

    @OriginalFileName
    @Column(name = "cover_image_original_file_name")
    private String coverImageOriginalFileName;

    public BookCoverImage(final Long bookId) {
        this.bookId = bookId;
    }
}
