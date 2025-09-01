package com.example.bankcards.dto.util;

import com.example.bankcards.dto.converter.UserMapper;
import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceDto {
    private final UserMapper userMapper;

    @Autowired
    public UserServiceDto(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User dtoToEntity(UserRequestDto userRequestDto) {
        return userMapper.dtoToEntity(userRequestDto);
    }

    public UserResponseDto entityToDto(User user) {
        return userMapper.entityToDto(user);
    }
}
