package com.example.notificationservice.model;

import com.example.notificationservice.eventStreaming.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "book_downloads_history")
@Getter
@Setter
@NoArgsConstructor
public class BookDownloadHistoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_download_history_item_id")
    private Long bookDownloadHistoryItemId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "genre")
    private String genre;

    public BookDownloadHistoryItem(final Book book) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.genre = book.getGenre();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDownloadHistoryItem item = (BookDownloadHistoryItem) o;
        return bookDownloadHistoryItemId != null ? bookDownloadHistoryItemId.equals(item.bookDownloadHistoryItemId) :
                item.bookDownloadHistoryItemId == null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass());
    }
}
