package com.example.bankcards.exception;

import com.example.bankcards.util.StatusCard;

public class IllegalCardStatusException extends RuntimeException {
    private final Long id;
    private final StatusCard statusCard;
    private final StatusCard updateStatusCard;

    public IllegalCardStatusException(Long id, StatusCard statusCard, StatusCard updateStatusCard) {
        super("Ошибка изменения статуса карты, проверьте текущий статус");
        this.id = id;
        this.statusCard = statusCard;
        this.updateStatusCard = updateStatusCard;
    }

    public Long getId() {
        return id;
    }
    public StatusCard getStatusCard() {
        return statusCard;
    }
    public StatusCard getUpdateStatusCard() {
        return updateStatusCard;
    }
}
