package com.example.bankcards.exception;

public class IllegalUserException extends RuntimeException {
    public IllegalUserException(String message) {
        super(message);
    }
    public IllegalUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
