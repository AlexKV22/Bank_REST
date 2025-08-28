package com.example.bankcards.dto.converter;

import com.example.bankcards.dto.dtoRequest.RoleRequestDto;
import com.example.bankcards.dto.dtoResponse.RoleResponseDto;
import com.example.bankcards.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "roleName", source = "roleName")
    RoleResponseDto entityToDto(Role role);


    @Mapping(target = "roleName", source = "roleName")
    Role dtoToEntity(RoleRequestDto roleRequestDto);
}
