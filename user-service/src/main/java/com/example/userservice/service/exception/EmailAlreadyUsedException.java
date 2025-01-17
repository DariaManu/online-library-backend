package com.example.userservice.service.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() {}
    public EmailAlreadyUsedException(final String message) {
        super(message);
    }
}
