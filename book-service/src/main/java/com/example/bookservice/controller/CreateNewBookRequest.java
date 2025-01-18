package com.example.bookservice.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CreateNewBookRequest implements Serializable {
    private String title;
    private String author;
    private String genre;
}
