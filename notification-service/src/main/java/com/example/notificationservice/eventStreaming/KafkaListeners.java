package com.example.notificationservice.eventStreaming;

import com.example.notificationservice.model.BookDownloadHistoryItem;
import com.example.notificationservice.model.BookDownloadHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaListeners {
    private BookDownloadHistoryRepository repository;

    @KafkaListener(topics = "book", groupId = "olb")
    void listener(Book data) {
        log.info("bookId - {} ; title - {} ; author - {} ; genre - {}", data.getBookId(), data.getTitle(), data.getAuthor(), data.getGenre());
        repository.save(new BookDownloadHistoryItem(data));
    }
}
