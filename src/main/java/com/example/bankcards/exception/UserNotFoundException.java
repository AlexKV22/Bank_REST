package com.example.bankcards.exception;

public class UserNotFoundException extends RuntimeException {
    private final String name;

    public UserNotFoundException(String name) {
        super("Пользователь не найден в базе данных");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
