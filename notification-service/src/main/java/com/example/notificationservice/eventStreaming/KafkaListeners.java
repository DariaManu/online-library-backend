package com.example.notificationservice.eventStreaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaListeners {

    @KafkaListener(topics = "book", groupId = "olb")
    void listener(Book data) {
        log.info("bookId - {} ; title - {} ; author - {} ; genre - {}", data.getBookId(), data.getTitle(), data.getAuthor(), data.getGenre());
    }
}
