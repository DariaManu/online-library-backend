package com.example.bookservice.messaging;

import com.example.bookservice.messaging.event.NewBookAddedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventDispatcher {
    private RabbitTemplate rabbitTemplate;
    private String newBookAddedExchange;
    private String newBookAddedRoutingKey;

    @Autowired
    EventDispatcher(final RabbitTemplate rabbitTemplate,
                    @Value("${newBookAdded.exchange}") final String newBookAddedExchange,
                    @Value("${newBook.added.key}") final String newBookAddedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.newBookAddedExchange = newBookAddedExchange;
        this.newBookAddedRoutingKey = newBookAddedRoutingKey;
    }

    public void send(final NewBookAddedEvent event) {
        rabbitTemplate.convertAndSend(newBookAddedExchange, newBookAddedRoutingKey, event);
    }
}
