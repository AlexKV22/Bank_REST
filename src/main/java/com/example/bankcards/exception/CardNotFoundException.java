package com.example.bankcards.exception;

public class CardNotFoundException extends RuntimeException {
    private final String cardNumber;
    private final String name;

    public CardNotFoundException(String cardNumber, String name) {
        super("Карта не найдена");
        this.cardNumber = cardNumber;
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public String getName() {
        return name;
    }
}
