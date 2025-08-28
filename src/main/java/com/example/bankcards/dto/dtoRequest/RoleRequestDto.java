package com.example.bankcards.dto.dtoRequest;

import com.example.bankcards.util.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO из запроса для работы с ролями")
public record RoleRequestDto (
        @NotNull(message = "Роль не может быть null")
        @Schema(description = "Название роли владельца карт")
        RoleName roleName){}
