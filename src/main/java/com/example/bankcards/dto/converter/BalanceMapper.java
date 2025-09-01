package com.example.bankcards.dto.converter;

import com.example.bankcards.dto.dtoResponse.BalanceResponse;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(target = "balance", source = "balance")
    BalanceResponse balanceToResponse(Card card);
}
