package com.example.bankcards.dto.dtoResponse;

import com.example.bankcards.util.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO из ответа для работы с переводами")
public record TransferResponseDto(
        @NotNull(message = "Дата перевода не может быть null")
        @Schema(description = "Дата окончания действия карты")
        LocalDate transferDate,

        @NotNull(message = "Сумма перевода не может быть null")
        @PositiveOrZero(message = "Сумма перевода может быть либо 0, либо положительный")
        @Digits(integer = 10, fraction = 2)
        @Schema(description = "Сумма перевода")
        BigDecimal transferAmount,

        @NotNull(message = "Карта отправителя не может быть null")
        @Schema(description = "Карта отправителя пользователя")
        CardResponseDto sender,

        @NotNull(message = "Карта получателя не может быть null")
        @Schema(description = "Карта получателя пользователя")
        CardResponseDto recipient,

        @NotNull(message = "Статус перевода не может быть null")
        @Schema(description = "Статус переводаа")
        TransferStatus transferStatus
)
{}
