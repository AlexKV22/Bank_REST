package com.example.bankcards.service.cardService;

import com.example.bankcards.entity.Card;
import com.example.bankcards.util.StatusCard;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    Page<Card> getAllCards(int page, int size);
    Card createCard(Card card, String name);
    Card changeStatusCard(Long id, String name, StatusCard statusCard);
    void deleteCard(Long id, String name);
    Page<Card> findAllCardsForUser(String name, int page, int size);
    Card findCardByNumberAndUserNameForUser(String number, String name);
    Card setStatusToBlockCardForUser(String number, String name);
    List<Card> updateBalanceToTransfer(Card from, Card to, BigDecimal amount);
}
