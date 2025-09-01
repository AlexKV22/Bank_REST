package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.PageResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.dto.util.PageServiceDto;
import com.example.bankcards.service.userService.UserService;
import com.example.bankcards.dto.util.UserServiceDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/admin/users")
public class AdminController {
    private final UserService userService;
    private final UserServiceDto userServiceDto;
    private final PageServiceDto pageServiceDto;

    @Autowired
    public AdminController(UserService userService, UserServiceDto userServiceDto, PageServiceDto pageServiceDto) {
        this.userService = userService;
        this.userServiceDto = userServiceDto;
        this.pageServiceDto = pageServiceDto;
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<UserResponseDto>> findAllUsers(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Page<User> allUsers = userService.findAllUsers(page, size);
        return ResponseEntity.ok(pageServiceDto.toPageResponse(allUsers, userServiceDto::entityToDto));
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserResponseDto> findUserByName(@PathVariable String name) {
        User user = userService.findUserByName(name);
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
