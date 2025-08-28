package com.example.bankcards.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO из ответа для отображения информации о пользователе в информации о картах")
public record UserResponseDtoForCards (
        @NotNull(message = "Имя пользователя не может быть null")
        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(max = 50)
        @Schema(description = "Имя пользователя")
        String name
)
{}
