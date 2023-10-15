package com.example.courseservice.exceptions;

public class SuccessHandler extends RuntimeException {
    public SuccessHandler() {}

    public SuccessHandler(String message) {
        super(message);
    }

    public SuccessHandler(String message, Throwable cause) {
        super(message, cause);
    }
}
