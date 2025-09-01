package com.example.bankcards.exception;

public class UserExistException extends RuntimeException {
    private final String name;

    public UserExistException(String name) {
        super("Имя пользователя занято");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
