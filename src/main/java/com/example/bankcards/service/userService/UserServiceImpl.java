package com.example.bankcards.service.userService;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.exception.UserExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.roleService.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<User> pageUsers = userRepository.findAll(pageable);
            if (pageUsers.isEmpty()) {
                throw new EmptyDatabaseException("Полученный список пользователей из базы данных пустой");
            }
            logger.info("Список пользователей из базы данных успешно получен и список не пуст");
            return pageUsers;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByName(String name) {
        return userRepository.findByName(name).orElseThrow(() -> new UserNotFoundException(name));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        Set<Role> roles = user.getRoles().stream().map(role -> roleService.findByName(role.getRoleName())).collect(Collectors.toSet());
            if (userRepository.existsByName(user.getName())) {
                throw new UserExistException(user.getName());
            }
            String encodePassword = passwordEncoder.encode(user.getPassword());
            user.setRoles(roles);
            user.setPassword(encodePassword);
            User newUser = userRepository.save(user);
            logger.info("Успешно создан новый пользователь.");
            return newUser;
    }

    @Override
    @Transactional
    public User updateUser(User user, String name) {
        Set<Role> roles = user.getRoles().stream().map(role -> roleService.findByName(role.getRoleName())).collect(Collectors.toSet());
            if (userRepository.existsByName(user.getName())) {
                throw new UserExistException(user.getName());
            }
            String encodePassword = passwordEncoder.encode(user.getPassword());
            Optional<User> userByName = userRepository.findByName(name);
            if (userByName.isPresent()) {
                logger.info("Успешно получен пользователь из базы данных.");
                User getUser = userByName.get();
                getUser.setPassword(encodePassword);
                getUser.setRoles(roles);
                getUser.setName(user.getName());
                User updatedUser = userRepository.save(getUser);
                logger.info("Данные пользователя {} успешно обновлены.", updatedUser.getName());
                return updatedUser;
            } else {
                throw new UserNotFoundException(name);
            }
    }

    @Override
    @Transactional
    public void deleteUser(String name) {
            Optional<User> userByName = userRepository.findByName(name);
            if (userByName.isPresent()) {
                logger.info("Успешно получен пользователь из базы данных.");
                User getUser = userByName.get();
                userRepository.delete(getUser);
                logger.info("Пользователь с именем {} успешно удален.", name);
            } else {
                throw new UserNotFoundException(name);
            }
    }
}
