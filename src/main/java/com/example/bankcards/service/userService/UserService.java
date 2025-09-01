package com.example.bankcards.service.userService;

import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;


public interface UserService {
    Page<User> findAllUsers(int page, int size);
    User findUserByName(String name);
    User createUser(User user);
    User updateUser(User user, String name);
    void deleteUser(String name);
}
