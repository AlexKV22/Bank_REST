package com.example.bankcards.service.cardService;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardExistsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.IllegalCardStatusException;
import com.example.bankcards.exception.IllegalUserException;
import com.example.bankcards.exception.NoAccessDatabaseException;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.userService.UserService;
import com.example.bankcards.util.StatusCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<Card> getAllCards() {
        try {
            List<Card> allCards = cardRepository.findAll();
            if (allCards.isEmpty()) {
                logger.warn("Полученный список карт из базы данных пустой");
                throw new EmptyDatabaseException("Полученный список карт из базы данных пустой");
            }
            logger.debug("Список карт из базы данных успешно получен и список не пуст");
            return allCards;
        } catch (DataAccessException e) {
            logger.error("Не удалось получить список карт, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
        }
    }

    @Override
    @Transactional
    public Card createCard(Card card, String name) {
        User user = userService.findByName(name);
        logger.debug("Пользователь из базы данных для назначения карты успешно получен");
        try {
            if (cardRepository.existsByNumber(card.getNumber())) {
                logger.warn("Карта с номером {} уже существует", card.getNumber());
                throw new CardExistsException("Такая карта уже существует");
            }
            card.setUser(user);
            Card newCard = cardRepository.save(card);
            logger.info("Новая карта для юзера {} успешно сохранена", user.getName());
            return newCard;
        } catch (DataAccessException e) {
            logger.error("Не удалось создать новую карту, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных", e.getCause());
        }
    }

    @Override
    @Transactional
    public Card changeStatusCard(Long id, String name, StatusCard statusCard) {
        Card card = findById(id);
        logger.debug("Карта с id {} успешно найдена", id);
        if (card.getUser().getName().equals(name)) {
            logger.debug("Переданное имя пользователя совпадает с именем пользователя карты из базы данных");
            try {
                switch (statusCard) {
                    case ACTIVE: {
                        if (card.getStatusCard() == StatusCard.ACTIVE) {
                            logger.info("Карта с id {} уже активирована", id);
                        } else if (card.getStatusCard() != StatusCard.BLOCKED) {
                            logger.warn("Карта с id {} не может быть активирована из статуса {}", id, card.getStatusCard());
                            throw new IllegalCardStatusException("Карта не может быть активирована не из статуса BLOCKED");
                        } else {
                            card.setStatusCard(StatusCard.ACTIVE);
                            card = cardRepository.save(card);
                            logger.info("Карта с id {} успешно активирована", id);
                        }
                        break;
                    }
                    case BLOCKED: {
                        if (card.getStatusCard() == StatusCard.BLOCKED) {
                            logger.info("Карта с id {} уже заблокирована", id);
                        } else {
                            card.setStatusCard(StatusCard.BLOCKED);
                            card = cardRepository.save(card);
                            logger.info("Карта с id {} успешно заблокирована", id);
                        }
                        break;
                    }
                    case EXPIRED: {
                        if (card.getStatusCard() == StatusCard.EXPIRED) {
                            logger.info("Карта с id {} уже в статусе просроченной ", id);
                        } else {
                            card.setStatusCard(StatusCard.EXPIRED);
                            card = cardRepository.save(card);
                            logger.info("Карте с id {} успешно установлен статус просроченной", id);
                        }
                    }
                }
            } catch (DataAccessException e) {
                logger.error("Не удалось установить новый статус карты, проверьте доступ к базе данных.");
                throw new NoAccessDatabaseException("Не удалось установить новый статус карты, проверьте доступ к базе данных.", e.getCause());
            }
        } else {
            logger.warn("Карта с id {} не принадлежит пользователю с именем {}", id, name);
            throw new IllegalUserException("Карта не принадлежит пользователю");
        }
        return card;
    }

    @Override
    @Transactional
    public void deleteCard(Long id, String name) {
        Card card = findById(id);
        logger.debug("Карта с id {} успешно найдена", id);
        if (card.getUser().getName().equals(name)) {
            try {
                logger.debug("Переданное имя пользователя совпадает с именем пользователя карты из базы данных");
                cardRepository.delete(card);
                logger.info("Карта с id {} успешно удалена", id);
            } catch (DataAccessException e) {
                logger.error("Не удалось удалить карту, проверьте доступ к базе данных.");
                throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
            }
        } else {
            logger.warn("Карта с id {} не принадлежит пользователю с именем {}", id, name);
            throw new IllegalUserException("Карта не принадлежит пользователю");
        }
    }

    @Transactional(readOnly = true)
    public Card findById(Long id) {
        try {
            return cardRepository.findById(id).orElseThrow(() -> {
                logger.warn("Карта с id {} не найдена", id);
                return new CardNotFoundException("Карта не найдена");
            });
        } catch (DataAccessException e) {
            logger.error("Не удалось найти карту, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
        }
    }

    @Transactional(readOnly = true)
    public List<Card> findAllCards(String name) {
        try {
            return cardRepository.findCardsByName(name);
        } catch (DataAccessException e) {
            logger.error("Не удалось найти список карт пользователя, проверьте доступ к базе данных.");
            throw new NoAccessDatabaseException("Не удалось выполнить операцию, проверьте доступ к базе данных.", e.getCause());
        }
    }
}
