package com.example.bankcards.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "DTO из запроса для работы с пользователями")
public record UserRequestDto (
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
        @Schema(description = "Список ролей пользователя")
        Set<RoleRequestDto> roles) {}
