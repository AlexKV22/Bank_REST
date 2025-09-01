package com.example.bankcards.dto.dtoRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "DTO из запроса для работы с переводами")
public record TransferRequestDto (
        @NotNull(message = "Сумма перевода не может быть null")
        @PositiveOrZero(message = "Сумма перевода может быть либо 0, либо положительный")
        @Digits(integer = 10, fraction = 2)
        @Schema(description = "Сумма перевода")
        BigDecimal amount,

        @NotNull(message = "Карта отправителя не может быть null")
        @Schema(description = "Карта отправителя пользователя")
        CardRequestDto cardSender,

        @NotNull(message = "Карта получателя не может быть null")
        @Schema(description = "Карта получателя пользователя")
        CardRequestDto cardRecipient) {}
