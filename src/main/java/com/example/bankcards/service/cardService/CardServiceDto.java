package com.example.bankcards.service.cardService;

import com.example.bankcards.dto.converter.CardMapper;
import com.example.bankcards.dto.dtoRequest.CardRequestDto;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.entity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceDto {
    private final CardMapper cardMapper;

    @Autowired
    public CardServiceDto(CardMapper cardMapper) {
        this.cardMapper = cardMapper;
    }

    public Card dtoToEntity(CardRequestDto cardRequestDto) {
        return cardMapper.dtoToEntity(cardRequestDto);
    }

    public CardResponseDto entityToDto(Card card) {
        return cardMapper.entityToDto(card);
    }

    public List<CardResponseDto> entityToDtoList(List<Card> cards) {
        return cards.stream().map(card -> cardMapper.entityToDto(card)).toList();
    }
}
