package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.exception.UserExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.roleService.RoleServiceImpl;
import com.example.bankcards.service.userService.UserServiceImpl;
import com.example.bankcards.util.RoleName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private RoleServiceImpl roleService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User userMisha;
    private User userDasha;
    private Role roleAdmin;
    private Role roleUser;



    @BeforeEach
    void setUp() {
        Set<Role> rolesForMisha = new HashSet<>();
        Set<Role> rolesForDasha = new HashSet<>();
        roleAdmin = new Role(1L, RoleName.ADMIN);
        roleUser = new Role(2L, RoleName.USER);
        rolesForMisha.add(roleAdmin);
        rolesForMisha.add(roleUser);
        rolesForDasha.add(roleUser);
        userMisha = User.builder().id(1L).name("Misha").roles(rolesForMisha).password("admin").build();
        userDasha = User.builder().id(2L).name("Dasha").roles(rolesForDasha).password("user").build();
    }

    @Test
    void findAllUsersNotEmptyTest() {
        List<User> listUsers = List.of(userMisha, userDasha);
        Page<User> cards = new PageImpl<>(listUsers);
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(cards);
        Page<User> allCards = userService.findAllUsers(0, 10);
        Assertions.assertFalse(allCards.isEmpty());
        List<User> content = allCards.getContent();
        Assertions.assertFalse(content.isEmpty());
        Assertions.assertTrue(content.contains(userMisha));
        Assertions.assertTrue(content.contains(userDasha));
        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void getAllUsersEmptyTest() {
        Page<User> users = new PageImpl<>(List.of());
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(users);
        Assertions.assertThrows(EmptyDatabaseException.class, () -> userService.findAllUsers(0, 10));
        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void findValidUserByNameTest() {
        Mockito.when(userRepository.findByName(userMisha.getName())).thenReturn(Optional.of(userMisha));
        User userByName = userService.findUserByName(userMisha.getName());
        Assertions.assertNotNull(userByName);
        Assertions.assertEquals(userMisha.getName(), userByName.getName());
        Assertions.assertEquals(userMisha.getId(), userByName.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findByName(userMisha.getName());
    }

    @Test
    void findInvalidUserByNameTest() {
        Mockito.when(userRepository.findByName(userMisha.getName())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findUserByName(userMisha.getName()));
        Mockito.verify(userRepository, Mockito.times(1)).findByName(userMisha.getName());
    }

    @Test
    void createValidUserTest() {
        Mockito.when(roleService.findByName(RoleName.USER)).thenReturn(roleUser);
        Mockito.when(roleService.findByName(RoleName.ADMIN)).thenReturn(roleAdmin);
        Mockito.when(userRepository.existsByName(userMisha.getName())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(userMisha.getPassword())).thenReturn("admin");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userMisha);
        User user = userService.createUser(userMisha);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(userMisha.getName(), user.getName());
        Assertions.assertEquals(userMisha.getPassword(), user.getPassword());
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.USER);
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.ADMIN);
        Mockito.verify(userRepository, Mockito.times(1)).existsByName(userMisha.getName());
    }

    @Test
    void createInvalidNameUserTest() {
        Mockito.when(roleService.findByName(RoleName.USER)).thenReturn(roleUser);
        Mockito.when(roleService.findByName(RoleName.ADMIN)).thenReturn(roleAdmin);
        Mockito.when(userRepository.existsByName(userMisha.getName())).thenReturn(true);
        Assertions.assertThrows(UserExistException.class, () -> userService.createUser(userMisha));
        Mockito.verify(userRepository, Mockito.times(1)).existsByName(userMisha.getName());
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.USER);
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.ADMIN);
    }

    @Test
    void updateValidUserTest() {
        User updatedUser = User.builder().name("Volodya").roles(Set.of(roleUser, roleAdmin)).password("anotheradmin").build();
        Mockito.when(roleService.findByName(RoleName.USER)).thenReturn(roleUser);
        Mockito.when(roleService.findByName(RoleName.ADMIN)).thenReturn(roleAdmin);
        Mockito.when(userRepository.existsByName(updatedUser.getName())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("anotheradmin");
        Mockito.when(userRepository.findByName(userMisha.getName())).thenReturn(Optional.of(userMisha));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);
        User user = userService.updateUser(updatedUser, userMisha.getName());
        Assertions.assertNotNull(user);
        Assertions.assertEquals(updatedUser.getName(), user.getName());
        Assertions.assertEquals(updatedUser.getPassword(), user.getPassword());
        Assertions.assertEquals(updatedUser.getRoles(), user.getRoles());
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.USER);
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.ADMIN);
        Mockito.verify(userRepository, Mockito.times(1)).existsByName(updatedUser.getName());
    }

    @Test
    void updateInvalidUserTest() {
        User updatedUser = User.builder().name("Volodya").roles(Set.of(roleUser, roleAdmin)).password("anotheradmin").build();
        Mockito.when(roleService.findByName(RoleName.USER)).thenReturn(roleUser);
        Mockito.when(roleService.findByName(RoleName.ADMIN)).thenReturn(roleAdmin);
        Mockito.when(userRepository.existsByName(updatedUser.getName())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("admin");
        Mockito.when(userRepository.findByName(userMisha.getName())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(updatedUser, userMisha.getName()));
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.USER);
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.ADMIN);
        Mockito.verify(userRepository, Mockito.times(1)).existsByName(updatedUser.getName());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(updatedUser.getPassword());
    }

    @Test
    void updateValidUserAndExistNameTest() {
        User updatedUser = User.builder().name("Volodya").roles(Set.of(roleUser, roleAdmin)).password("anotheradmin").build();
        Mockito.when(roleService.findByName(RoleName.USER)).thenReturn(roleUser);
        Mockito.when(roleService.findByName(RoleName.ADMIN)).thenReturn(roleAdmin);
        Mockito.when(userRepository.existsByName(updatedUser.getName())).thenReturn(true);
        Assertions.assertThrows(UserExistException.class, () -> userService.updateUser(updatedUser, userMisha.getName()));
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.USER);
        Mockito.verify(roleService, Mockito.times(1)).findByName(RoleName.ADMIN);
    }

    @Test
    void deleteValidUserTest() {
        Mockito.when(userRepository.findByName(userMisha.getName())).thenReturn(Optional.of(userMisha));
        Mockito.doNothing().when(userRepository).delete(userMisha);
        Assertions.assertDoesNotThrow(() -> userService.deleteUser(userMisha.getName()));
        Mockito.verify(userRepository, Mockito.times(1)).findByName(userMisha.getName());
    }

    @Test
    void deleteInvalidUserTest() {
        Mockito.when(userRepository.findByName(userMisha.getName())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userMisha.getName()));
        Mockito.verify(userRepository, Mockito.times(1)).findByName(userMisha.getName());
    }
}
