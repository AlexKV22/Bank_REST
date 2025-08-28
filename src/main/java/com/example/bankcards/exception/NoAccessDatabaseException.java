package com.example.bankcards.exception;

public class NoAccessDatabaseException extends RuntimeException {
    public NoAccessDatabaseException(String message) {
        super(message);
    }
    public NoAccessDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
