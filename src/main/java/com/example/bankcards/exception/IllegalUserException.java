package com.example.bankcards.exception;

public class IllegalUserException extends RuntimeException {
    private final Long id;
    private final String username;

    public IllegalUserException(Long id, String username) {
        super("Карта не принадлежит пользователю");
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
}
