package com.example.courseservice.exceptions;

public class EmptyException extends RuntimeException {
    public EmptyException() {
    }

    public EmptyException(String message) {
        super(message);
    }

    public EmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
