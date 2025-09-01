package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoRequest.RoleRequestDto;
import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.dto.dtoResponse.PageResponseDto;
import com.example.bankcards.dto.dtoResponse.RoleResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDtoForCards;
import com.example.bankcards.dto.util.PageServiceDto;
import com.example.bankcards.dto.util.UserServiceDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.exception.UserExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.service.userService.UserServiceImpl;
import com.example.bankcards.util.RoleName;
import com.example.bankcards.util.StatusCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {
    private User userMisha;
    private User userDasha;
    private UserResponseDto userResponseDto;
    private UserResponseDto userResponseDtoForLesha;
    private PageResponseDto<UserResponseDto> pageResponseDto;
    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDtoForLesha;
    private Set<Role> rolesForMisha;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private UserServiceDto userServiceDto;
    @MockBean
    private PageServiceDto pageServiceDto;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        rolesForMisha = new HashSet<>();
        Set<Role> rolesForDasha = new HashSet<>();
        Role roleAdmin = new Role(1L, RoleName.ADMIN);
        Role roleUser = new Role(2L, RoleName.USER);
        rolesForMisha.add(roleAdmin);
        rolesForMisha.add(roleUser);
        rolesForDasha.add(roleUser);
        userMisha = User.builder().id(1L).name("Misha").roles(rolesForMisha).password("admin").build();
        userDasha = User.builder().id(2L).name("Dasha").roles(rolesForDasha).password("user").build();
        CardResponseDto cardResponseDto = new CardResponseDto(1L, "56276732",
                new UserResponseDtoForCards("Misha"), LocalDate.now(),
                BigDecimal.valueOf(100.00), StatusCard.ACTIVE);
        RoleRequestDto roleRequestDto = new RoleRequestDto(RoleName.ADMIN);
        RoleResponseDto roleResponseDto = new RoleResponseDto(1L, RoleName.ADMIN);
        userResponseDto = new UserResponseDto(1L, "Misha", "admin", Set.of(roleResponseDto), Set.of(cardResponseDto));
        userResponseDtoForLesha = new UserResponseDto(1L, "Lesha", "admin", Set.of(roleResponseDto), Set.of(cardResponseDto));
        userRequestDto = new UserRequestDto("Misha", "admin", Set.of(roleRequestDto));
        userRequestDtoForLesha = new UserRequestDto("Lesha", "admin", Set.of(roleRequestDto));
        pageResponseDto = new PageResponseDto<>(List.of(userResponseDto), 0, 10,5L,0, true);

    }


    @Test
    void findAllUsersTest() throws Exception {
        Page<User> allUsers = new PageImpl<>(List.of(userMisha, userDasha));
        Mockito.when(userService.findAllUsers(0, 10)).thenReturn(allUsers);
        Mockito.when(userServiceDto.entityToDto(any(User.class))).thenReturn(userResponseDto);
        Mockito.when(pageServiceDto.toPageResponse(any(Page.class), any(Function.class))).thenReturn(pageResponseDto);
        mockMvc.perform(get("/admin/users").queryParam("page", "0").queryParam("size", "10")).andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.content[0].name").value("Misha"));
        Mockito.verify(userService, Mockito.times(1)).findAllUsers(0,10);
    }

    @Test
    void findAllUsersEmptyTest() throws Exception {
        Mockito.when(userService.findAllUsers(0, 10)).thenThrow(EmptyDatabaseException.class);
        mockMvc.perform(get("/admin/users").queryParam("page", "0").queryParam("size", "10")).andExpect(status().isNotFound());
        Mockito.verify(userService, Mockito.times(1)).findAllUsers(0,10);
    }

    @Test
    void findAllUsersNoDatabaseAccessTest() throws Exception {
        Mockito.when(userService.findAllUsers(0, 10)).thenThrow(RecoverableDataAccessException.class);
        mockMvc.perform(get("/admin/users").queryParam("page", "0").queryParam("size", "10")).andExpect(status().isInternalServerError());
        Mockito.verify(userService, Mockito.times(1)).findAllUsers(0,10);
    }

    @Test
    void findValidUserByNameTest() throws Exception {
        Mockito.when(userService.findUserByName("Misha")).thenReturn(userMisha);
        Mockito.when(userServiceDto.entityToDto(any(User.class))).thenReturn(userResponseDto);
        mockMvc.perform(get("/admin/users/{name}", "Misha")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Misha")).andExpect(jsonPath("$.id").value(1));
        Mockito.verify(userService, Mockito.times(1)).findUserByName("Misha");
    }

    @Test
    void findInvalidUserByNameTest() throws Exception {
        Mockito.when(userService.findUserByName("Misha")).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/admin/users/{name}", "Misha")).andExpect(status().isNotFound());
        Mockito.verify(userService, Mockito.times(1)).findUserByName("Misha");
    }

    @Test
    void createValidUser() throws Exception {
        Mockito.when(userServiceDto.dtoToEntity(any(UserRequestDto.class))).thenReturn(userMisha);
        Mockito.when(userService.createUser(userMisha)).thenReturn(userMisha);
        Mockito.when(userServiceDto.entityToDto(any(User.class))).thenReturn(userResponseDto);
        mockMvc.perform(post("/admin/users").content(objectMapper.writeValueAsBytes(userRequestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Misha")).andExpect(jsonPath("$.id").value(1));
        Mockito.verify(userService, Mockito.times(1)).createUser(userMisha);
    }

    @Test
    void createInvalidUser() throws Exception {
        Mockito.when(userServiceDto.dtoToEntity(any(UserRequestDto.class))).thenReturn(userMisha);
        Mockito.when(userService.createUser(userMisha)).thenThrow(UserExistException.class);
        mockMvc.perform(post("/admin/users").content(objectMapper.writeValueAsBytes(userRequestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        Mockito.verify(userService, Mockito.times(1)).createUser(userMisha);
    }

    @Test
    void updateValidUser() throws Exception {
        User newLesha = User.builder().id(1L).name("Lesha").roles(rolesForMisha).password("admin").build();
        Mockito.when(userServiceDto.dtoToEntity(any(UserRequestDto.class))).thenReturn(newLesha);
        Mockito.when(userService.updateUser(userMisha, "Misha" )).thenReturn(newLesha);
        Mockito.when(userServiceDto.entityToDto(any(User.class))).thenReturn(userResponseDtoForLesha);
        mockMvc.perform(put("/admin/users/{name}", "Misha").content(objectMapper.writeValueAsBytes(userRequestDtoForLesha)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Lesha")).andExpect(jsonPath("$.id").value(1));
        Mockito.verify(userService, Mockito.times(1)).updateUser(userMisha, "Misha");
    }

    @Test
    void updateValidButExistNameUser() throws Exception {
        User newLesha = User.builder().id(1L).name("Lesha").roles(rolesForMisha).password("admin").build();
        Mockito.when(userServiceDto.dtoToEntity(any(UserRequestDto.class))).thenReturn(newLesha);
        Mockito.when(userService.updateUser(newLesha, "Misha")).thenThrow(UserExistException.class);
        mockMvc.perform(put("/admin/users/{name}", "Misha").content(objectMapper.writeValueAsBytes(userRequestDtoForLesha)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        Mockito.verify(userService, Mockito.times(1)).updateUser(newLesha, "Misha");
    }

    @Test
    void updateInvalidUser() throws Exception {
        User newLesha = User.builder().id(1L).name("Lesha").roles(rolesForMisha).password("admin").build();
        Mockito.when(userServiceDto.dtoToEntity(any(UserRequestDto.class))).thenReturn(newLesha);
        Mockito.when(userService.updateUser(newLesha, "Misha")).thenThrow(UserNotFoundException.class);
        mockMvc.perform(put("/admin/users/{name}", "Misha").content(objectMapper.writeValueAsBytes(userRequestDtoForLesha)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        Mockito.verify(userService, Mockito.times(1)).updateUser(newLesha, "Misha");
    }

    @Test
    void deleteValidUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser("Misha");
        mockMvc.perform(delete("/admin/users/{name}", "Misha")).andExpect(status().isNoContent());
        Mockito.verify(userService, Mockito.times(1)).deleteUser("Misha");
    }

    @Test
    void deleteInvalidUser() throws Exception {
        Mockito.doThrow(UserNotFoundException.class).when(userService).deleteUser("Misha");
        mockMvc.perform(delete("/admin/users/{name}", "Misha")).andExpect(status().isNotFound());
        Mockito.verify(userService, Mockito.times(1)).deleteUser("Misha");
    }
}
