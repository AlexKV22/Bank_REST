package com.example.bankcards.service.userService;

import com.example.bankcards.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User findByName(String name);
    User createUser(User user);
    User updateUser(User user, String name);
    void deleteUser(String name);
}
