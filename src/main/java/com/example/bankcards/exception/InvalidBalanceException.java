package com.example.bankcards.exception;

public class InvalidBalanceException extends RuntimeException {
    private final String cardNumber;

    public InvalidBalanceException(String cardNumber) {
        super("Недостаточно среств для перевода");
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
