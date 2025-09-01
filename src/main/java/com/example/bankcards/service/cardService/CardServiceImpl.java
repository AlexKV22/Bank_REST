package com.example.bankcards.service.cardService;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardExistsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.IllegalCardStatusException;
import com.example.bankcards.exception.IllegalUserException;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.userService.UserService;
import com.example.bankcards.util.CardMaskUtil;
import com.example.bankcards.util.StatusCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {
    private final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);
    private final CardRepository cardRepository;
    private final UserService userService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Card> getAllCards(int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("number").ascending());
            Page<Card> allCards = cardRepository.findAll(pageable);
            if (allCards.isEmpty()) {
                throw new EmptyDatabaseException("Полученный список карт из базы данных пустой");
            }
            logger.info("Успешно получен список карт");
            return allCards;
    }

    @Override
    @Transactional
    public Card createCard(Card card, String name) {
        User user = userService.findUserByName(name);
        logger.info("Успешно получен пользователь для создания карты");
            if (cardRepository.existsByNumber(card.getNumber())) {
                throw new CardExistsException(card.getNumber());
            }
            card.setUser(user);
            Card newCard = cardRepository.save(card);
            logger.info("Новая карта для юзера {} успешно сохранена", user.getName());
            return newCard;
    }

    @Override
    @Transactional
    public Card changeStatusCard(Long id, String name, StatusCard statusCard) {
        Card updatedCard = null;
            Optional<Card> cardByIdAndUserName = cardRepository.findCardByIdAndUserName(id, name);
            if (cardByIdAndUserName.isPresent()) {
                logger.info("Карта с id {} успешно найдена и принадлежит пользователю {}", id, name);
                Card card = cardByIdAndUserName.get();
                switch (statusCard) {
                    case ACTIVE: {
                        if (card.getStatusCard() == StatusCard.ACTIVE) {
                            logger.info("Карта с id {} уже активирована", id);
                            updatedCard = card;
                        } else if (card.getStatusCard() != StatusCard.BLOCKED) {
                            throw new IllegalCardStatusException(id, card.getStatusCard(), statusCard);
                        } else {
                            card.setStatusCard(StatusCard.ACTIVE);
                            updatedCard = cardRepository.save(card);
                            logger.info("Карта с id {} успешно активирована", id);
                        }
                        break;
                    }
                    case BLOCKED: {
                        if (card.getStatusCard() == StatusCard.BLOCKED) {
                            logger.info("Карта с id {} уже заблокирована", id);
                            updatedCard = card;
                        } else {
                            card.setStatusCard(StatusCard.BLOCKED);
                            updatedCard = cardRepository.save(card);
                            logger.info("Карта с id {} успешно заблокирована", id);
                        }
                        break;
                    }
                    case EXPIRED: {
                        if (card.getStatusCard() == StatusCard.EXPIRED) {
                            logger.info("Карта с id {} уже в статусе просроченной ", id);
                            updatedCard = card;
                        } else if (card.getStatusCard() == StatusCard.BLOCKED) {
                            throw new IllegalCardStatusException(id, card.getStatusCard(), statusCard);
                        } else {
                            card.setStatusCard(StatusCard.EXPIRED);
                            updatedCard = cardRepository.save(card);
                            logger.info("Карте с id {} успешно установлен статус просроченной", id);
                        }
                        break;
                    }
                    default: {
                        throw new IllegalCardStatusException(card.getId(), card.getStatusCard(), statusCard);
                    }
                }
            } else {
                throw new IllegalUserException(id, name);
            }
        return updatedCard;
    }

    @Override
    @Transactional
    public void deleteCard(Long id, String name) {
        Optional<Card> cardByIdAndUserName = cardRepository.findCardByIdAndUserName(id, name);
        if (cardByIdAndUserName.isPresent()) {
            logger.info("Карта с id {} успешно найдена и принадлежит пользователю {}", id, name);
            Card card = cardByIdAndUserName.get();
            cardRepository.delete(card);
            logger.info("Карта с id {} успешно удалена", id);
        } else {
            throw new IllegalUserException(id, name);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Card> findAllCardsForUser(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("number").ascending());
        return cardRepository.findCardsByUserName(name, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Card findCardByNumberAndUserNameForUser(String number, String name) {
        return cardRepository.findCardByNumberAndUserName(number, name).orElseThrow(() -> new CardNotFoundException(number, name));
    }

    @Override
    @Transactional
    public Card setStatusToBlockCardForUser(String number, String name) {
        Card changedStatusForUser = null;
        Optional<Card> cardByNumberAndUserName = cardRepository.findCardByNumberAndUserName(number, name);
        if (cardByNumberAndUserName.isPresent()) {
            logger.info("Карта с номером {} успешно найдена и принадлежит пользователю {}", CardMaskUtil.mask(number), name);
            Card card = cardByNumberAndUserName.get();
            if (card.getStatusCard() == StatusCard.REQUIRED_BLOCK) {
                logger.info("Карта с номером {} уже в статусе запроса на блокировку", CardMaskUtil.mask(number));
                changedStatusForUser = card;
            } else if (card.getStatusCard() == StatusCard.BLOCKED) {
                logger.info("Карта с номером {} уже заблокирована", CardMaskUtil.mask(number));
                changedStatusForUser = card;
            } else {
                card.setStatusCard(StatusCard.REQUIRED_BLOCK);
                changedStatusForUser = cardRepository.save(card);
                logger.info("Карте с номером {} успешно установлен запрос на блокировку", CardMaskUtil.mask(number));
            }
            return changedStatusForUser;
        } else {
            throw new CardNotFoundException(number, name);
        }
    }

    @Transactional
    public List<Card> updateBalanceToTransfer(Card from, Card to, BigDecimal amount) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        Card cardFrom = cardRepository.save(from);
        Card cardTo = cardRepository.save(to);
        logger.info("Успешно сохранены измененные балансы карт с номерами отправителя: {}, и получателя {}", CardMaskUtil.mask(from.getNumber()), CardMaskUtil.mask(to.getNumber()));
        return List.of(cardFrom, cardTo);
    }
}