package com.example.bankcards.service.cardService;

import com.example.bankcards.entity.Card;
import com.example.bankcards.util.StatusCard;

import java.util.List;

public interface CardService {
    List<Card> getAllCards();
    Card createCard(Card card, String name);
    Card changeStatusCard(Long id, String name, StatusCard statusCard);
    void deleteCard(Long id, String name);
    List<Card> findAllCards(String name);
}
