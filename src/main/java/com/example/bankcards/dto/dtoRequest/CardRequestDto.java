package com.example.bankcards.dto.dtoRequest;

import com.example.bankcards.util.StatusCard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO из запроса для работы с картами")
public record CardRequestDto (
        @NotNull(message = "Номер карты не может быть null")
        @NotBlank(message = "Номер карты не может быть пустым")
        @Size(max = 19, min = 19)
        @Schema(description = "Уникальный номер карты")
        String number,

        @NotNull(message = "Дата окончания действия карты не может быть null")
        @FutureOrPresent(message = "Дата окончания действия карты должна быть либо будущей, либо текущей датой")
        @Schema(description = "Дата окончания действия карты")
        LocalDate expiryDate,

        @NotNull(message = "Баланс карты не может быть null")
        @PositiveOrZero(message = "Баланс карты может быть либо 0, либо положительный")
        @Digits(integer = 10, fraction = 2)
        @Schema(description = "Баланс карты")
        BigDecimal balance,

        @NotNull(message = "Статус карты не может быть null")
        @Schema(description = "Статус карты")
        StatusCard statusCard){}
