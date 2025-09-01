package com.example.bankcards.dto.util;

import com.example.bankcards.dto.converter.BalanceMapper;
import com.example.bankcards.dto.dtoResponse.BalanceResponse;
import com.example.bankcards.entity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BalanceServiceDto {

    private final BalanceMapper balanceMapper;

    @Autowired
    public BalanceServiceDto(BalanceMapper balanceMapper) {
        this.balanceMapper = balanceMapper;
    }

    public BalanceResponse getBalanceResponse(Card card) {
        return balanceMapper.balanceToResponse(card);
    }
}
