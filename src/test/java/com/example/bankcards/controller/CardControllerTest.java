package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoRequest.CardRequestDto;
import com.example.bankcards.dto.dtoRequest.RoleRequestDto;
import com.example.bankcards.dto.dtoRequest.UserRequestDto;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.dto.dtoResponse.PageResponseDto;
import com.example.bankcards.dto.dtoResponse.RoleResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDtoForCards;
import com.example.bankcards.dto.util.CardServiceDto;
import com.example.bankcards.dto.util.PageServiceDto;
import com.example.bankcards.dto.util.UserServiceDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardExistsException;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.exception.IllegalCardStatusException;
import com.example.bankcards.exception.IllegalUserException;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CardControllerTest {
    private User userMisha;
    private PageResponseDto<CardResponseDto> pageResponseDto;
    private Card mishaCard;
    private CardResponseDto cardResponseDto;
    private CardRequestDto cardRequestDto;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CardServiceImpl cardService;
    @MockBean
    private CardServiceDto cardServiceDto;
    @MockBean
    private PageServiceDto pageServiceDto;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mishaCard = new Card(1L, "1111111111111111111", userMisha, LocalDate.now(), BigDecimal.valueOf(400.00), StatusCard.ACTIVE, List.of(), List.of());
        cardResponseDto = new CardResponseDto(1L, "1111111111111111111",
                new UserResponseDtoForCards("Misha"), LocalDate.now(),
                BigDecimal.valueOf(400.00), StatusCard.ACTIVE);
        cardRequestDto = new CardRequestDto("1111111111111111111", LocalDate.now(), BigDecimal.valueOf(400.00), StatusCard.ACTIVE);
        Set<Role> rolesForMisha = new HashSet<>();
        Role roleAdmin = new Role(1L, RoleName.ADMIN);
        Role roleUser = new Role(2L, RoleName.USER);
        rolesForMisha.add(roleAdmin);
        rolesForMisha.add(roleUser);
        userMisha = User.builder().id(1L).name("Misha").roles(rolesForMisha).password("admin").build();
        pageResponseDto = new PageResponseDto<>(List.of(cardResponseDto), 0, 10,5L,0, true);
    }

    @Test
    void getAllCardsTest() throws Exception {
        Page<Card> allCards = new PageImpl<>(List.of(mishaCard));
        Mockito.when(cardService.getAllCards(0, 10)).thenReturn(allCards);
        Mockito.when(cardServiceDto.entityToDto(any(Card.class))).thenReturn(cardResponseDto);
        Mockito.when(pageServiceDto.toPageResponse(any(Page.class), any(Function.class))).thenReturn(pageResponseDto);
        mockMvc.perform(get("/admin/cards").queryParam("page", "0").queryParam("size", "10")).andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.content[0].number").value("1111111111111111111"));
        Mockito.verify(cardService, Mockito.times(1)).getAllCards(0,10);
    }

    @Test
    void getAllCardsEmptyTest() throws Exception {
        Mockito.when(cardService.getAllCards(0, 10)).thenThrow(EmptyDatabaseException.class);
        mockMvc.perform(get("/admin/cards").queryParam("page", "0").queryParam("size", "10")).andExpect(status().isNotFound());
        Mockito.verify(cardService, Mockito.times(1)).getAllCards(0,10);
    }

    @Test
    void getAllCardsNoDatabaseAccessTest() throws Exception {
        Mockito.when(cardService.getAllCards(0, 10)).thenThrow(RecoverableDataAccessException.class);
        mockMvc.perform(get("/admin/cards").queryParam("page", "0").queryParam("size", "10")).andExpect(status().isInternalServerError());
        Mockito.verify(cardService, Mockito.times(1)).getAllCards(0,10);
    }

    @Test
    void createValidCardTest() throws Exception {
        Mockito.when(cardServiceDto.dtoToEntity(cardRequestDto)).thenReturn(mishaCard);
        Mockito.when(cardService.createCard(mishaCard,"Misha")).thenReturn(mishaCard);
        Mockito.when(cardServiceDto.entityToDto(any(Card.class))).thenReturn(cardResponseDto);
        mockMvc.perform(post("/admin/cards/{name}/card", "Misha").content(objectMapper.writeValueAsBytes(cardRequestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.number").value("1111111111111111111")).andExpect(jsonPath("$.balance").value(BigDecimal.valueOf(400.00)));
        Mockito.verify(cardService, Mockito.times(1)).createCard(mishaCard,"Misha");
    }

    @Test
    void createInvalidCardTest() throws Exception {
        Mockito.when(cardServiceDto.dtoToEntity(cardRequestDto)).thenReturn(mishaCard);
        Mockito.when(cardService.createCard(mishaCard,"Misha")).thenThrow(CardExistsException.class);
        mockMvc.perform(post("/admin/cards/{name}/card", "Misha").content(objectMapper.writeValueAsBytes(cardRequestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        Mockito.verify(cardService, Mockito.times(1)).createCard(mishaCard,"Misha");
    }

    @Test
    void changeValidStatusCardTest() throws Exception {
        Card newMishaCard = new Card(1L, "32463434", userMisha, LocalDate.now(), BigDecimal.valueOf(400.00), StatusCard.BLOCKED, List.of(), List.of());
        CardResponseDto newCardResponseDto = new CardResponseDto(1L, "32463434",
                new UserResponseDtoForCards("Misha"), LocalDate.now(),
                BigDecimal.valueOf(400.00), StatusCard.BLOCKED);
        Mockito.when(cardService.changeStatusCard(1L, "Misha", StatusCard.BLOCKED)).thenReturn(newMishaCard);
        Mockito.when(cardServiceDto.entityToDto(any(Card.class))).thenReturn(newCardResponseDto);
        mockMvc.perform(put("/admin/cards/{name}/card/{id}/status/{statusCard}", "Misha", 1, StatusCard.BLOCKED)).andExpect(status().isOk()).andExpect(jsonPath("$.number").value("32463434")).andExpect(jsonPath("$.id").value(1));
        Mockito.verify(cardService, Mockito.times(1)).changeStatusCard(1L, "Misha", StatusCard.BLOCKED);
    }

    @Test
    void changeInvalidStatusCardTest() throws Exception {
        Mockito.when(cardService.changeStatusCard(1L, "Misha", StatusCard.BLOCKED)).thenThrow(IllegalCardStatusException.class);
        mockMvc.perform(put("/admin/cards/{name}/card/{id}/status/{statusCard}", "Misha", 1, StatusCard.BLOCKED)).andExpect(status().isBadRequest());
        Mockito.verify(cardService, Mockito.times(1)).changeStatusCard(1L, "Misha", StatusCard.BLOCKED);
    }

    @Test
    void deleteValidCard() throws Exception {
        Mockito.doNothing().when(cardService).deleteCard(1L, "Misha");
        mockMvc.perform(delete("/admin/cards/{name}/card/{id}", "Misha", 1L)).andExpect(status().isNoContent());
        Mockito.verify(cardService, Mockito.times(1)).deleteCard(1L, "Misha");
    }

    @Test
    void deleteInvalidCard() throws Exception {
        Mockito.doThrow(IllegalUserException.class).when(cardService).deleteCard(1L, "Misha");
        mockMvc.perform(delete("/admin/cards/{name}/card/{id}", "Misha", 1L)).andExpect(status().isBadRequest());
        Mockito.verify(cardService, Mockito.times(1)).deleteCard(1L, "Misha");
    }
}
