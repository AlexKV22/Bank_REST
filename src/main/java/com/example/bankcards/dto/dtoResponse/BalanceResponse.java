package com.example.bankcards.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "DTO из ответа для отображения баланса карты")
public record BalanceResponse(
        @NotNull(message = "Баланс карты не может быть null")
        @PositiveOrZero(message = "Баланс карты может быть либо 0, либо положительный")
        @Digits(integer = 10, fraction = 2)
        @Schema(description = "Баланс карты")
        BigDecimal balance
) {}
