package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoResponse.BalanceResponse;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.dto.dtoResponse.PageResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.dto.util.BalanceServiceDto;
import com.example.bankcards.dto.util.PageServiceDto;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.dto.util.CardServiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {

    private final CardService cardService;
    private final CardServiceDto cardServiceDto;
    private final PageServiceDto pageServiceDto;
    private final BalanceServiceDto balanceServiceDto;

    @Autowired
    public UserController(CardService cardService, CardServiceDto cardServiceDto, PageServiceDto pageServiceDto, BalanceServiceDto balanceServiceDto ) {
        this.cardService = cardService;
        this.cardServiceDto = cardServiceDto;
        this.pageServiceDto = pageServiceDto;
        this.balanceServiceDto = balanceServiceDto;
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<CardResponseDto>> getAllCardsUser(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            Principal principal) {
        Page<Card> allCards = cardService.findAllCardsForUser(principal.getName(), page, size);
        return ResponseEntity.ok(pageServiceDto.toPageResponse(allCards, cardServiceDto::entityToDto));
    }

    @GetMapping("/card")
    public ResponseEntity<CardResponseDto> getCardByNumber(@RequestParam String number, Principal principal) {
        Card cardByNumber = cardService.findCardByNumberAndUserNameForUser(number, principal.getName());
        return ResponseEntity.ok(cardServiceDto.entityToDto(cardByNumber));
    }

    @GetMapping("/card/balance")
    public ResponseEntity<BalanceResponse> getBalance(@RequestParam String number, Principal principal) {
        Card cardByNumber = cardService.findCardByNumberAndUserNameForUser(number, principal.getName());
        return ResponseEntity.ok(balanceServiceDto.getBalanceResponse(cardByNumber));
    }

    @PostMapping("/card/block")
    public ResponseEntity<CardResponseDto> setStatusToBlockCard(@RequestParam String number, Principal principal) {
        Card requiredStatusBlock = cardService.setStatusToBlockCardForUser(number, principal.getName());
        return ResponseEntity.ok(cardServiceDto.entityToDto(requiredStatusBlock));
    }
}

