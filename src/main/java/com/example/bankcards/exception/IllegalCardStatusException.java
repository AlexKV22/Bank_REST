package com.example.bankcards.exception;

public class IllegalCardStatusException extends RuntimeException {
    public IllegalCardStatusException(String message) {
        super(message);
    }
    public IllegalCardStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
