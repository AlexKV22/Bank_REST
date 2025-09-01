package com.example.bankcards.exception;

public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException() {
        super("Неверный JWT токен при валидации");
    }
}
