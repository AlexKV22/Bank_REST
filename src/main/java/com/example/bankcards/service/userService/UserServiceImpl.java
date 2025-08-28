package com.example.bankcards.service.userService;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.exception.NoAccessDatabaseException;
import com.example.bankcards.exception.UserExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.roleService.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<User> findAllUsers() {
        try {
            List<User> allUsers = userRepository.findAll();
            if (allUsers.isEmpty()) {
                logger.warn("Полученный список пользователей из базы данных пустой");
                throw new EmptyDatabaseException("Полученный список пользователей из базы данных пустой");
            }
            logger.debug("Список пользователей из базы данных успешно получен и список не пуст");
            return allUsers;
        } catch (DataAccessException e) {
            logger.warn("Не удалось получить список юзеров, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findByName(String name) {
        try {
            return userRepository.findByName(name).orElseThrow(() -> {
                    logger.warn("Пользователь с именем {} не найден", name);
                    return new UserNotFoundException("Пользователь не найден в базе данных");
            });
        } catch (DataAccessException e) {
            logger.warn("Не удалось найти юзера, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
        }
    }

    @Override
    @Transactional
    public User createUser(User user) {
        Set<Role> roles = user.getRoles().stream().map(role -> roleService.findByName(role.getRoleName())).collect(Collectors.toSet());
        try {
            if (userRepository.findByName(user.getName()).isPresent()) {
                logger.warn("Пользователь с именем {} уже существует", user.getName());
                throw new UserExistException("Пользователь уже существует");
            }
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User newUser = userRepository.save(user);
            logger.info("Успешно создан новый пользователь.");
            return newUser;
        } catch (DataAccessException e) {
            logger.warn("Не удалось создать нового пользователя, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
        }
    }

    @Override
    @Transactional
    public User updateUser(User user, String name) {
        Set<Role> roles = user.getRoles().stream().map(role -> roleService.findByName(role.getRoleName())).collect(Collectors.toSet());
        try {
            User userByName = findByName(name);
            logger.debug("Успешно получен пользователь из базы данных.");
            if (userRepository.findByName(user.getName()).isPresent()) {
                logger.warn("Пользователь с именем {} уже существует", user.getName());
                throw new UserExistException("Пользователь уже существует");
            }
            userByName.setName(user.getName());
            userByName.setRoles(roles);
            userByName.setPassword(passwordEncoder.encode(user.getPassword()));
            User updatedUser = userRepository.save(userByName);
            logger.info("Данные пользователя {} успешно обновлены.", updatedUser.getName());
            return updatedUser;
        } catch (DataAccessException e) {
            logger.warn("Не удалось обновить данные пользователя, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
        }
    }

    @Override
    @Transactional
    public void deleteUser(String name) {
        try {
            User userByName = findByName(name);
            logger.debug("Успешно получен пользователь из базы данных.");
            userRepository.delete(userByName);
            logger.info("Пользователь с именем {} успешно удален.", name);
        } catch (DataAccessException e) {
            logger.warn("Не удалось удалить пользователя, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось удалить пользователя, проверьте доступ к базе данных.", e.getCause());
        }
    }
}
