package com.example.bankcards.exception;

public class CardExistsException extends RuntimeException {
    public CardExistsException(String message) {
        super(message);
    }
    public CardExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
