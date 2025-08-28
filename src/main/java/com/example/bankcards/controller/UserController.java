package com.example.bankcards.controller;

import com.example.bankcards.dto.converter.CardMapper;
import com.example.bankcards.dto.converter.UserMapper;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.dto.dtoResponse.UserResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.service.cardService.CardServiceDto;
import com.example.bankcards.service.userService.UserService;
import com.example.bankcards.service.userService.UserServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserServiceDto userServiceDto;
    private final CardService cardService;
    private final CardServiceDto cardServiceDto;

    @Autowired
    public UserController(UserService userService, UserServiceDto userServiceDto, CardService cardService, CardServiceDto cardServiceDto ) {
        this.userService = userService;
        this.userServiceDto = userServiceDto;
        this.cardService = cardService;
        this.cardServiceDto = cardServiceDto;
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getAllCardsUser(Principal principal) {
        List<Card> allCards = cardService.findAllCards(principal.getName());
        List<CardResponseDto> collect = cardServiceDto.entityToDtoList(allCards);
        return ResponseEntity.ok(collect);
    }
}
