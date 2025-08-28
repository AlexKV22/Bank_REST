package com.example.bankcards.dto.dtoResponse;

import com.example.bankcards.util.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO из ответа для работы с ролями")
public record RoleResponseDto (
        @NotNull(message = "ID роли не может быть null")
        @Schema(description = "ID роли")
        Long id,

        @NotNull(message = "Роль не может быть null")
        @Schema(description = "Название роли пользователя")
        RoleName roleName){}
