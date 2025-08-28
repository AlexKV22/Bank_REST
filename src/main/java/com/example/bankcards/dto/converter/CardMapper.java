package com.example.bankcards.dto.converter;

import com.example.bankcards.dto.dtoRequest.CardRequestDto;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserForCardsMapper.class)
public interface CardMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "expiryDate", source = "expiryDate")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "statusCard", source = "statusCard")
    @Mapping(target = "user", source = "user")
    CardResponseDto entityToDto(Card card);


    @Mapping(target = "number", source = "number")
    @Mapping(target = "expiryDate", source = "expiryDate")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "statusCard", source = "statusCard")
    Card dtoToEntity(CardRequestDto cardRequestDto);
}
