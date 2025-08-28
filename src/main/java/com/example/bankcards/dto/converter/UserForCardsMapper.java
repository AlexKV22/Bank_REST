package com.example.bankcards.dto.converter;

import com.example.bankcards.dto.dtoResponse.UserResponseDtoForCards;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserForCardsMapper {
    @Mapping(target = "name", source = "name")
    UserResponseDtoForCards entityToDto(User user);
}
