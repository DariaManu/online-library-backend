package com.example.notificationservice.eventStreaming;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Book implements Serializable {
    private Long bookId;
    private String title;
    private String author;
    private String genre;
}
