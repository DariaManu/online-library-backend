package com.example.bookservice.service.exception;

public class MissingObjectException extends RuntimeException {
    public MissingObjectException() {}

    public MissingObjectException(String message) {
        super(message);
    }
}
