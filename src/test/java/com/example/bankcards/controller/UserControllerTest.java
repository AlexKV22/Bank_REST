package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoRequest.RoleRequestDto;
import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.BalanceResponse;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.dto.dtoResponse.PageResponseDto;
import com.example.bankcards.dto.dtoResponse.RoleResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDtoForCards;
import com.example.bankcards.dto.util.BalanceServiceDto;
import com.example.bankcards.dto.util.CardServiceDto;
import com.example.bankcards.dto.util.PageServiceDto;
import com.example.bankcards.dto.util.UserServiceDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.exception.UserExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.service.cardService.CardServiceImpl;
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
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    private User userMisha;
    private PageResponseDto<CardResponseDto> pageResponseDto;
    private Card mishaCard;
    private CardResponseDto cardResponseDto;
    private BalanceResponse balanceResponse;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CardServiceImpl cardService;
    @MockBean
    private CardServiceDto cardServiceDto;
    @MockBean
    private PageServiceDto pageServiceDto;
    @MockBean
    private BalanceServiceDto balanceServiceDto;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        balanceResponse = new BalanceResponse(BigDecimal.valueOf(400.00));
        mishaCard = new Card(1L, "1111111111111111111", userMisha, LocalDate.now(), BigDecimal.valueOf(400.00), StatusCard.ACTIVE, List.of(), List.of());
        cardResponseDto = new CardResponseDto(1L, "1111111111111111111",
                new UserResponseDtoForCards("Misha"), LocalDate.now(),
                BigDecimal.valueOf(400.00), StatusCard.ACTIVE);
        Set<Role> rolesForMisha = new HashSet<>();
        Role roleAdmin = new Role(1L, RoleName.ADMIN);
        Role roleUser = new Role(2L, RoleName.USER);
        rolesForMisha.add(roleAdmin);
        rolesForMisha.add(roleUser);
        userMisha = User.builder().id(1L).name("Misha").roles(rolesForMisha).password("admin").build();
        pageResponseDto = new PageResponseDto<>(List.of(cardResponseDto), 0, 10,5L,0, true);
    }

    @Test
    void getAllCardsUserTest() throws Exception {
        Principal mockPrincipal = () -> "Misha";
        Page<Card> allCards = new PageImpl<>(List.of(mishaCard));
        Mockito.when(cardService.findAllCardsForUser("Misha",0, 10)).thenReturn(allCards);
        Mockito.when(cardServiceDto.entityToDto(any(Card.class))).thenReturn(cardResponseDto);
        Mockito.when(pageServiceDto.toPageResponse(any(Page.class), any(Function.class))).thenReturn(pageResponseDto);
        mockMvc.perform(get("/user").principal(mockPrincipal).queryParam("page", "0").queryParam("size", "10")).andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.content[0].number").value("1111111111111111111"));
        Mockito.verify(cardService, Mockito.times(1)).findAllCardsForUser("Misha",0,10);
    }

    @Test
    void findValidCardByNumberAndUserNameForUserTest() throws Exception {
        Principal mockPrincipal = () -> "Misha";
        Mockito.when(cardService.findCardByNumberAndUserNameForUser("1111111111111111111", "Misha")).thenReturn(mishaCard);
        Mockito.when(cardServiceDto.entityToDto(any(Card.class))).thenReturn(cardResponseDto);
        mockMvc.perform(get("/user/card").principal(mockPrincipal).queryParam("number", "1111111111111111111")).andExpect(status().isOk()).andExpect(jsonPath("$.number").value("1111111111111111111"));
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumberAndUserNameForUser("1111111111111111111", "Misha");
    }

    @Test
    void findInvalidCardByNumberAndUserNameForUserTest() throws Exception {
        Principal mockPrincipal = () -> "Misha";
        Mockito.when(cardService.findCardByNumberAndUserNameForUser("1111111111111111111", "Misha")).thenThrow(CardNotFoundException.class);
        mockMvc.perform(get("/user/card").principal(mockPrincipal).queryParam("number", "1111111111111111111")).andExpect(status().isNotFound());
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumberAndUserNameForUser("1111111111111111111", "Misha");
    }

    @Test
    void getBalanceTest() throws Exception {
        Principal mockPrincipal = () -> "Misha";
        Mockito.when(cardService.findCardByNumberAndUserNameForUser("1111111111111111111", "Misha")).thenReturn(mishaCard);
        Mockito.when(balanceServiceDto.getBalanceResponse(any(Card.class))).thenReturn(balanceResponse);
        mockMvc.perform(get("/user/card/balance").principal(mockPrincipal).queryParam("number", "1111111111111111111")).andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(BigDecimal.valueOf(400.00)));
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumberAndUserNameForUser("1111111111111111111", "Misha");
    }
}
