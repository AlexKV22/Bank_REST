package com.example.bankcards.exception;


public class CardExistsException extends RuntimeException {
    private final String cardNumber;

    public CardExistsException(String cardNumber) {
        super("Такая карта уже существует");
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
