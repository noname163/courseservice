package com.example.courseservice.exceptions;

import java.io.IOException;

public class MediaUploadException extends RuntimeException {
    public MediaUploadException() {
    }

    public MediaUploadException(String message) {
        super(message);
    }

    public MediaUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
