package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.service.roleService.RoleServiceImpl;
import com.example.bankcards.util.RoleName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @InjectMocks
    private RoleServiceImpl roleService;
    @Mock
    private RoleRepository roleRepository;


    @Test
    void findByExistNameTest() {
        Role role = new Role(1L, RoleName.ADMIN);
        Mockito.when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(Optional.of(role));
        Role byName = roleService.findByName(role.getRoleName());
        Assertions.assertEquals(role.getRoleName(), byName.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(role.getRoleName());
    }

    @Test
    void findByNotExistNameTest() {
        Role role = new Role(1L, RoleName.ADMIN);
        Mockito.when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(Optional.empty());
        Assertions.assertThrows(RoleNotFoundException.class, () -> roleService.findByName(role.getRoleName()));
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(role.getRoleName());
    }
}
