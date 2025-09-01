package com.example.bankcards.dto.converter;

import com.example.bankcards.dto.dtoResponse.PageResponseDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface PageMapper {

    default <T,R> PageResponseDto<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream().map(mapper).toList();
        return new PageResponseDto<>(content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
