package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardExistsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.EmptyDatabaseException;
import com.example.bankcards.exception.IllegalCardStatusException;
import com.example.bankcards.exception.IllegalUserException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.cardService.CardServiceImpl;
import com.example.bankcards.service.userService.UserServiceImpl;
import com.example.bankcards.util.RoleName;
import com.example.bankcards.util.StatusCard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @InjectMocks
    private CardServiceImpl cardService;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserServiceImpl userService;

    private User userMisha;
    private Card cardMisha;
    private Card cardDasha;
    private Card newCard;

    @BeforeEach
    void setUp() {
        cardMisha = Card.builder().id(1L)
                .number("222")
                .balance(BigDecimal.valueOf(540.00))
                .statusCard(StatusCard.ACTIVE)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        cardDasha = Card.builder().id(2L)
                .number("111")
                .balance(BigDecimal.valueOf(1540.00))
                .statusCard(StatusCard.ACTIVE)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        newCard = Card.builder().id(3L)
                .number("333")
                .balance(BigDecimal.valueOf(1540.00))
                .statusCard(StatusCard.ACTIVE)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();

        HashSet<Role> rolesForMisha = new HashSet<>();
        Role roleAdmin = new Role(1L, RoleName.ADMIN);
        rolesForMisha.add(roleAdmin);
        userMisha = User.builder().id(1L).name("Misha").roles(rolesForMisha).password("admin").build();
    }

    @Test
    void getAllCardsNotEmptyTest() {
        List<Card> listCards = List.of(cardMisha, cardDasha);
        Page<Card> cards = new PageImpl<>(listCards);
        Mockito.when(cardRepository.findAll(Mockito.any(Pageable.class))).thenReturn(cards);
        Page<Card> allCards = cardService.getAllCards(0, 10);
        Assertions.assertFalse(allCards.isEmpty());
        List<Card> content = allCards.getContent();
        Assertions.assertFalse(content.isEmpty());
        Assertions.assertTrue(content.contains(cardMisha));
        Assertions.assertTrue(content.contains(cardDasha));
        Mockito.verify(cardRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void getAllCardsEmptyTest() {
        Page<Card> cards = new PageImpl<>(List.of());
        Mockito.when(cardRepository.findAll(Mockito.any(Pageable.class))).thenReturn(cards);
        Assertions.assertThrows(EmptyDatabaseException.class, () -> cardService.getAllCards(0, 10));
        Mockito.verify(cardRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void createNotExistCardTest() {
        Mockito.when(userService.findUserByName("Misha")).thenReturn(userMisha);
        Mockito.when(cardRepository.existsByNumber(newCard.getNumber())).thenReturn(false);
        Mockito.when(cardRepository.save(newCard)).thenReturn(newCard);
        Card misha = cardService.createCard(newCard, "Misha");
        Assertions.assertNotNull(misha);
        Assertions.assertEquals(newCard.getNumber(), misha.getNumber());
        Assertions.assertEquals(newCard.getBalance(), misha.getBalance());
        Assertions.assertEquals(newCard.getStatusCard(), misha.getStatusCard());
        Assertions.assertEquals(newCard.getExpiryDate(), misha.getExpiryDate());
        Mockito.verify(cardRepository, Mockito.times(1)).save(newCard);
        Mockito.verify(userService, Mockito.times(1)).findUserByName("Misha");
        Mockito.verify(cardRepository, Mockito.times(1)).existsByNumber(newCard.getNumber());
    }

    @Test
    void createExistCardTest() {
        Mockito.when(userService.findUserByName("Misha")).thenReturn(userMisha);
        Mockito.when(cardRepository.existsByNumber(newCard.getNumber())).thenReturn(true);
        Assertions.assertThrows(CardExistsException.class, () -> cardService.createCard(newCard, "Misha"));
        Mockito.verify(cardRepository, Mockito.times(1)).existsByNumber(newCard.getNumber());
        Mockito.verify(userService, Mockito.times(1)).findUserByName("Misha");
    }

    @Test
    void changeStatusActiveCardWhenStatusCardIsActiveTest() {
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Card cardActive = cardService.changeStatusCard(1L, "Misha", StatusCard.ACTIVE);
        Assertions.assertNotNull(cardActive);
        Assertions.assertEquals(cardMisha.getNumber(), cardActive.getNumber());
        Assertions.assertEquals(cardMisha.getBalance(), cardActive.getBalance());
        Assertions.assertEquals(cardMisha.getStatusCard(), cardActive.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusActiveCardWhenStatusCardIsExpiredTest() {
        cardMisha.setStatusCard(StatusCard.EXPIRED);
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Assertions.assertThrows(IllegalCardStatusException.class, () -> cardService.changeStatusCard(1L, "Misha", StatusCard.ACTIVE));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusActiveCardWhenStatusCardIsBlockedTest() {
        Card updatedCard = Card.builder().id(1L)
                .number("222")
                .balance(BigDecimal.valueOf(540.00))
                .statusCard(StatusCard.ACTIVE)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();

        cardMisha.setStatusCard(StatusCard.BLOCKED);
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Mockito.when(cardRepository.save(cardMisha)).thenReturn(updatedCard);
        Card cardBlocked = cardService.changeStatusCard(1L, "Misha", StatusCard.ACTIVE);
        Assertions.assertNotNull(cardBlocked);
        Assertions.assertEquals(updatedCard.getNumber(), cardBlocked.getNumber());
        Assertions.assertEquals(updatedCard.getBalance(), cardBlocked.getBalance());
        Assertions.assertEquals(updatedCard.getStatusCard(), cardBlocked.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusBlockedCardWhenStatusCardIsActiveTest() {
        Card updatedCard = Card.builder().id(1L)
                .number("222")
                .balance(BigDecimal.valueOf(540.00))
                .statusCard(StatusCard.BLOCKED)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Mockito.when(cardRepository.save(cardMisha)).thenReturn(updatedCard);
        Card cardBlocked = cardService.changeStatusCard(1L, "Misha", StatusCard.BLOCKED);
        Assertions.assertNotNull(cardBlocked);
        Assertions.assertEquals(updatedCard.getNumber(), cardBlocked.getNumber());
        Assertions.assertEquals(updatedCard.getBalance(), cardBlocked.getBalance());
        Assertions.assertEquals(updatedCard.getStatusCard(), cardBlocked.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusBlockedCardWhenStatusCardIsExpiredTest() {
        Card updatedCard = Card.builder().id(1L)
                .number("222")
                .balance(BigDecimal.valueOf(540.00))
                .statusCard(StatusCard.BLOCKED)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        cardMisha.setStatusCard(StatusCard.EXPIRED);
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Mockito.when(cardRepository.save(cardMisha)).thenReturn(updatedCard);
        Card cardBlocked = cardService.changeStatusCard(1L, "Misha", StatusCard.BLOCKED);
        Assertions.assertNotNull(cardBlocked);
        Assertions.assertEquals(updatedCard.getNumber(), cardBlocked.getNumber());
        Assertions.assertEquals(updatedCard.getBalance(), cardBlocked.getBalance());
        Assertions.assertEquals(updatedCard.getStatusCard(), cardBlocked.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusBlockedCardWhenStatusCardIsBlockedTest() {
        cardMisha.setStatusCard(StatusCard.BLOCKED);
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Card cardActive = cardService.changeStatusCard(1L, "Misha", StatusCard.BLOCKED);
        Assertions.assertNotNull(cardActive);
        Assertions.assertEquals(cardMisha.getNumber(), cardActive.getNumber());
        Assertions.assertEquals(cardMisha.getBalance(), cardActive.getBalance());
        Assertions.assertEquals(cardMisha.getStatusCard(), cardActive.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusExpiredCardWhenStatusCardIsActiveTest() {
        Card updatedCard = Card.builder().id(1L)
                .number("222")
                .balance(BigDecimal.valueOf(540.00))
                .statusCard(StatusCard.EXPIRED)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Mockito.when(cardRepository.save(cardMisha)).thenReturn(updatedCard);
        Card cardExpired = cardService.changeStatusCard(1L, "Misha", StatusCard.EXPIRED);
        Assertions.assertNotNull(cardExpired);
        Assertions.assertEquals(updatedCard.getNumber(), cardExpired.getNumber());
        Assertions.assertEquals(updatedCard.getBalance(), cardExpired.getBalance());
        Assertions.assertEquals(updatedCard.getStatusCard(), cardExpired.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusExpiredCardWhenStatusCardIsExpiredTest() {
        cardMisha.setStatusCard(StatusCard.EXPIRED);
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Card cardBlocked = cardService.changeStatusCard(1L, "Misha", StatusCard.EXPIRED);
        Assertions.assertNotNull(cardBlocked);
        Assertions.assertEquals(cardMisha.getNumber(), cardBlocked.getNumber());
        Assertions.assertEquals(cardMisha.getBalance(), cardBlocked.getBalance());
        Assertions.assertEquals(cardMisha.getStatusCard(), cardBlocked.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusExpiredCardWhenStatusCardIsBlockedTest() {
        cardMisha.setStatusCard(StatusCard.BLOCKED);
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Assertions.assertThrows(IllegalCardStatusException.class, () -> cardService.changeStatusCard(1L, "Misha", StatusCard.EXPIRED));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void changeStatusCardWhenNotFindCard() {
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.empty());
        Assertions.assertThrows(IllegalUserException.class, () -> cardService.changeStatusCard(1L, "Misha", StatusCard.ACTIVE));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void deleteValidCardTest() {
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.of(cardMisha));
        Mockito.doNothing().when(cardRepository).delete(cardMisha);
        Assertions.assertDoesNotThrow(() -> cardService.deleteCard(1L, "Misha"));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
        Mockito.verify(cardRepository, Mockito.times(1)).delete(cardMisha);
    }

    @Test
    void deleteInValidCardTest() {
        Mockito.when(cardRepository.findCardByIdAndUserName(1L, "Misha")).thenReturn(Optional.empty());
        Assertions.assertThrows(IllegalUserException.class, () -> cardService.deleteCard(1L, "Misha"));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByIdAndUserName(1L, "Misha");
    }

    @Test
    void findValidAllCardsForUserTest() {
        List<Card> listCards = List.of(cardMisha, cardDasha);
        Page<Card> cards = new PageImpl<>(listCards);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("number").ascending());
        Mockito.when(cardRepository.findCardsByUserName("Misha", pageable)).thenReturn(cards);
        Page<Card> allCards = cardService.findAllCardsForUser("Misha",0, 10);
        Assertions.assertFalse(allCards.isEmpty());
        List<Card> content = allCards.getContent();
        Assertions.assertFalse(content.isEmpty());
        Assertions.assertTrue(content.contains(cardMisha));
        Assertions.assertTrue(content.contains(cardDasha));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardsByUserName("Misha", pageable);
    }

    @Test
    void findInValidAllCardsForUserTest() {
        Page<Card> cards = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 10, Sort.by("number").ascending());
        Mockito.when(cardRepository.findCardsByUserName("Misha", pageable)).thenReturn(cards);
        Page<Card> allCards = cardService.findAllCardsForUser("Misha",0, 10);
        Assertions.assertTrue(allCards.isEmpty());
        List<Card> content = allCards.getContent();
        Assertions.assertTrue(content.isEmpty());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardsByUserName("Misha", pageable);
    }

    @Test
    void findCardByValidNumberAndValidUserNameForUserTest() {
        Mockito.when(cardRepository.findCardByNumberAndUserName(cardMisha.getNumber(),"Misha")).thenReturn(Optional.of(cardMisha));
        Card misha = cardService.findCardByNumberAndUserNameForUser(cardMisha.getNumber(), "Misha");
        Assertions.assertNotNull(misha);
        Assertions.assertEquals(cardMisha.getNumber(), misha.getNumber());
        Assertions.assertEquals(cardMisha.getBalance(), misha.getBalance());
        Assertions.assertEquals(cardMisha.getStatusCard(), misha.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByNumberAndUserName(cardMisha.getNumber(), "Misha");
    }

    @Test
    void findCardByInvalidNumberAndInvalidUserNameForUserTest() {
        Mockito.when(cardRepository.findCardByNumberAndUserName(cardMisha.getNumber(),"Misha")).thenReturn(Optional.empty());
        Assertions.assertThrows(CardNotFoundException.class, () -> cardService.findCardByNumberAndUserNameForUser(cardMisha.getNumber(), "Misha"));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByNumberAndUserName(cardMisha.getNumber(), "Misha");
    }

    @Test
    void setStatusToBlockValidCardForUserWhereStatusIsRequiredBlockTest() {
        cardMisha.setStatusCard(StatusCard.REQUIRED_BLOCK);
        Mockito.when(cardRepository.findCardByNumberAndUserName(cardMisha.getNumber(), "Misha")).thenReturn(Optional.of(cardMisha));
        Card misha = cardService.setStatusToBlockCardForUser(cardMisha.getNumber(), "Misha");
        Assertions.assertNotNull(misha);
        Assertions.assertEquals(cardMisha.getNumber(), misha.getNumber());
        Assertions.assertEquals(cardMisha.getBalance(), misha.getBalance());
        Assertions.assertEquals(cardMisha.getStatusCard(), misha.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByNumberAndUserName(cardMisha.getNumber(), "Misha");
    }

    @Test
    void setStatusToBlockValidCardForUserWhereStatusIsBlockedTest() {
        cardMisha.setStatusCard(StatusCard.BLOCKED);
        Mockito.when(cardRepository.findCardByNumberAndUserName(cardMisha.getNumber(), "Misha")).thenReturn(Optional.of(cardMisha));
        Card misha = cardService.setStatusToBlockCardForUser(cardMisha.getNumber(), "Misha");
        Assertions.assertNotNull(misha);
        Assertions.assertEquals(cardMisha.getNumber(), misha.getNumber());
        Assertions.assertEquals(cardMisha.getBalance(), misha.getBalance());
        Assertions.assertEquals(cardMisha.getStatusCard(), misha.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByNumberAndUserName(cardMisha.getNumber(), "Misha");
    }

    @Test
    void setStatusToBlockValidCardForUserWhereStatusIsAnotherTest() {
        Card updatedCard = Card.builder().id(1L)
                .number("222")
                .balance(BigDecimal.valueOf(540.00))
                .statusCard(StatusCard.REQUIRED_BLOCK)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        Mockito.when(cardRepository.findCardByNumberAndUserName(cardMisha.getNumber(), "Misha")).thenReturn(Optional.of(cardMisha));
        Mockito.when(cardRepository.save(cardMisha)).thenReturn(updatedCard);
        Card misha = cardService.setStatusToBlockCardForUser(cardMisha.getNumber(), "Misha");
        Assertions.assertNotNull(misha);
        Assertions.assertEquals(updatedCard.getNumber(), misha.getNumber());
        Assertions.assertEquals(updatedCard.getBalance(), misha.getBalance());
        Assertions.assertEquals(updatedCard.getStatusCard(), misha.getStatusCard());
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByNumberAndUserName(cardMisha.getNumber(), "Misha");
    }

    @Test
    void setStatusToBlockInvalidCardForUserTest() {
        Mockito.when(cardRepository.findCardByNumberAndUserName(cardMisha.getNumber(), "Misha")).thenReturn(Optional.empty());
        Assertions.assertThrows(CardNotFoundException.class, () -> cardService.setStatusToBlockCardForUser(cardMisha.getNumber(), "Misha"));
        Mockito.verify(cardRepository, Mockito.times(1)).findCardByNumberAndUserName(cardMisha.getNumber(), "Misha");
    }

    @Test
    void updateBalanceToValidTransferTest() {
        Mockito.when(cardRepository.save(cardMisha)).thenReturn(cardMisha);
        Mockito.when(cardRepository.save(cardDasha)).thenReturn(cardDasha);
        List<Card> cards = cardService.updateBalanceToTransfer(cardMisha, cardDasha, BigDecimal.valueOf(500.00));
        Assertions.assertNotNull(cards);
        Assertions.assertTrue(cards.contains(cardMisha));
        Assertions.assertTrue(cards.contains(cardDasha));
        Card cardForMisha = cards.stream().filter(card -> card.getNumber().equals("222")).findFirst().get();
        Card cardForDasha = cards.stream().filter(card -> card.getNumber().equals("111")).findFirst().get();
        Assertions.assertEquals(BigDecimal.valueOf(40.00), cardForMisha.getBalance());
        Assertions.assertEquals(BigDecimal.valueOf(2040.00), cardForDasha.getBalance());
        Mockito.verify(cardRepository, Mockito.times(1)).save(cardMisha);
        Mockito.verify(cardRepository, Mockito.times(1)).save(cardDasha);
    }
}
