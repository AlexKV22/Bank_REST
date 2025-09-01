package com.example.bankcards.dto.converter;

import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "cards", source = "cards")
    @Mapping(target = "password", source = "password")
    UserResponseDto entityToDto(User user);


    @Mapping(target = "name", source = "name")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "password", source = "password")
    User dtoToEntity(UserRequestDto userRequestDto);
}
