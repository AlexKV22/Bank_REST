package com.example.bankcards.exception;

public class EmptyDatabaseException extends RuntimeException {
    public EmptyDatabaseException(String message) {
        super(message);
    }
    public EmptyDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
