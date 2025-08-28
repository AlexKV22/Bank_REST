package com.example.bankcards.service.userService;

import com.example.bankcards.dto.converter.UserMapper;
import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

    public List<UserResponseDto> entityToDtoList(List<User> users) {
        return users.stream().map(user -> userMapper.entityToDto(user)).toList();
    }
}
