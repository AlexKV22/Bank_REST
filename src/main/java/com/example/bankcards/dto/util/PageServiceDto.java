package com.example.bankcards.dto.util;

import com.example.bankcards.dto.converter.PageMapper;
import com.example.bankcards.dto.dtoResponse.PageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PageServiceDto {
    private final PageMapper pageMapper;

    @Autowired
    public PageServiceDto(PageMapper pageMapper) {
        this.pageMapper = pageMapper;
    }

    public <T,R> PageResponseDto<R> toPageResponse (Page<T> page, Function<T, R> mapper) {
        return pageMapper.toPageResponse(page, mapper);
    }
}
