package com.example.courseservice.exceptions;

public class InValidAuthorizationException extends RuntimeException {
    public InValidAuthorizationException() {}

    public InValidAuthorizationException(String message) {
        super(message);
    }

    public InValidAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
