package com.example.bankcards.dto.util;

import com.example.bankcards.dto.converter.CardMapper;
import com.example.bankcards.dto.dtoRequest.CardRequestDto;
import com.example.bankcards.dto.dtoResponse.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardMaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
        card.setNumber(CardMaskUtil.mask(card.getNumber()));
        return cardMapper.entityToDto(card);
    }
}
