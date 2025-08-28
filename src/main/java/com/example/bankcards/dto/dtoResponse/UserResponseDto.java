package com.example.bankcards.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "DTO из ответа для работы с пользователя")
public record UserResponseDto (
        @NotNull(message = "ID пользователя не может быть null")
        @Schema(description = "ID пользователя")
        Long id,

        @NotNull(message = "Имя пользователя не может быть null")
        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(max = 50)
        @Schema(description = "Имя пользователя")
        String name,

        @NotNull(message = "Пароль пользователя не может быть null")
        @NotBlank(message = "Пароль пользователя не может быть пустым")
        @Size(max = 100)
        @Schema(description = "Пароль пользователя")
        String password,

        @NotNull(message = "Список ролей пользователя не может быть null")
        @Schema(description = "Список ролей владельца карты")
        Set<RoleResponseDto> roles,

        @NotNull(message = "Список карт пользователя не может быть null")
        @Schema(description = "Список карт пользователя")
        Set<CardResponseDto> cards
)
{}
