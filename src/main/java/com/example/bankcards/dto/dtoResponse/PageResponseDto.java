package com.example.bankcards.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "DTO из ответа для работы с картами")
public record PageResponseDto<T> (
        @NotNull(message = "Описаните page не может быть null")
        @Schema(description = "Описаните page")
        List<T> content,

        @NotNull(message = "Номер страницы не может быть null")
        @Schema(description = "Номер страницы")
        Integer page,

        @NotNull(message = "Количество элементов страницы не может быть null")
        @Schema(description = "Количество элементов страницы")
        Integer size,

        @NotNull(message = "Общее количество элементов не может быть null")
        @Schema(description = "Общее количество элементов")
        Long totalElements,

        @NotNull(message = "Общее количество страниц не может быть null")
        @Schema(description = "Общее количество страниц")
        Integer totalPages,

        @NotNull(message = "Проверка последней страницы не может быть null")
        @Schema(description = "Проверка последней страницы")
        Boolean last
) {}
