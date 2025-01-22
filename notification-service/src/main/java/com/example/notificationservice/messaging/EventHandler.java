package com.example.notificationservice.messaging;

import com.example.notificationservice.messaging.event.NewBookAddedEvent;
import com.example.notificationservice.sockets.Socket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventHandler {
    private final static ObjectMapper mapper = new ObjectMapper();

    @RabbitListener(queues = "${newBookAdded.queue}")
    void handleNewBookAddedEvent(final NewBookAddedEvent event) {
        System.out.println(event);
        try {
            String message = mapper.writeValueAsString(event);
            System.out.println(message);
            Socket.broadcast(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
