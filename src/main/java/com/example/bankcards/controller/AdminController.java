package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.userService.UserService;
import com.example.bankcards.service.userService.UserServiceDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminController {
    private final UserService userService;
    private final UserServiceDto userServiceDto;

    @Autowired
    public AdminController(UserService userService, UserServiceDto userServiceDto) {
        this.userService = userService;
        this.userServiceDto = userServiceDto;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        List<UserResponseDto> collect = userServiceDto.entityToDtoList(allUsers);
        return ResponseEntity.ok(collect);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String name) {
        User user = userService.findByName(name);
        return ResponseEntity.ok(userServiceDto.entityToDto(user));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User userFromDto = userServiceDto.dtoToEntity(userRequestDto);
        User user = userService.createUser(userFromDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}").buildAndExpand(user.getName()).toUri();
        return ResponseEntity.created(uri).body(userServiceDto.entityToDto(user));
    }

    @PutMapping("/{name}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("name") String name, @Valid @RequestBody UserRequestDto userRequestDto) {
        User userFromDto = userServiceDto.dtoToEntity(userRequestDto);
        User user = userService.updateUser(userFromDto, name);
        return ResponseEntity.ok(userServiceDto.entityToDto(user));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable String name) {
        userService.deleteUser(name);
        return ResponseEntity.noContent().build();
    }
}
