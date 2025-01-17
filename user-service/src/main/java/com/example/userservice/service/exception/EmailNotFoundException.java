package com.example.userservice.service.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {}
    public EmailNotFoundException(final String message) {
        super(message);
    }
}
