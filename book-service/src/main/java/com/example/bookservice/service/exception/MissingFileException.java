package com.example.bookservice.service.exception;

public class MissingFileException extends RuntimeException {
    public MissingFileException(){}

    public MissingFileException(String message) {
        super(message);
    }
}
