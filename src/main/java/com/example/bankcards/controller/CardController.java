package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoRequest.CardRequestDto;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.service.cardService.CardServiceDto;
import com.example.bankcards.util.StatusCard;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/cards")
public class CardController {
    private final CardService cardService;
    private final CardServiceDto cardServiceDto;

    @Autowired
    public CardController(CardService cardService, CardServiceDto cardServiceDto) {
        this.cardService = cardService;
        this.cardServiceDto = cardServiceDto;
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getAllCards() {
        List<Card> allCards = cardService.getAllCards();
        List<CardResponseDto> cardResponseDto = cardServiceDto.entityToDtoList(allCards);
        return ResponseEntity.ok(cardResponseDto);
    }

    @PostMapping("/{name}/card")
    public ResponseEntity<CardResponseDto> createCard(@PathVariable("name") String name, @Valid @RequestBody CardRequestDto cardRequestDto) {
        Card card = cardServiceDto.dtoToEntity(cardRequestDto);
        Card newCard = cardService.createCard(card, name);
        return ResponseEntity.ok(cardServiceDto.entityToDto(newCard));
    }

    @PutMapping("/{name}/card/{id}/status/{statusCard}")
    public ResponseEntity<CardResponseDto> changeStatusCard(@PathVariable("name") String name, @PathVariable("id") Long id, @PathVariable("statusCard") StatusCard statusCard) {
        Card blockedCard = cardService.changeStatusCard(id, name, statusCard);
        return ResponseEntity.ok(cardServiceDto.entityToDto(blockedCard));
    }

    @DeleteMapping("/{name}/card/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable("name") String name, @PathVariable("id") Long id) {
        cardService.deleteCard(id, name);
        return ResponseEntity.noContent().build();
    }
}
