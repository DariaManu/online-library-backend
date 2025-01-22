package com.example.notificationservice.messaging.event;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NewBookAddedEvent implements Serializable {
    private String eventMessage;
    private Long bookId;
    private String title;
    private String author;
    private String genre;
}
