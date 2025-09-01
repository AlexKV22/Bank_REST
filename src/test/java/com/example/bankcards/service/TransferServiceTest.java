package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidBalanceException;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.cardService.CardServiceImpl;
import com.example.bankcards.service.transferService.TransferServiceImpl;
import com.example.bankcards.util.StatusCard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;


@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @InjectMocks
    private TransferServiceImpl transferService;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private CardServiceImpl cardService;

    private Card cardMisha;
    private Card cardMishaAnother;
    private Transfer transfer;

    @BeforeEach
    void setUp() {
        cardMisha = Card.builder().id(1L)
                .number("222")
                .balance(BigDecimal.valueOf(540.00))
                .statusCard(StatusCard.ACTIVE)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        cardMishaAnother = Card.builder().id(2L)
                .number("111")
                .balance(BigDecimal.valueOf(1540.00))
                .statusCard(StatusCard.ACTIVE)
                .expiryDate(LocalDate.of(2045, 7, 22))
                .build();
        User misha = User.builder().name("Misha").build();

        transfer = Transfer.builder().id(1L)
                .transferDate(LocalDate.now())
                .transferAmount(BigDecimal.valueOf(100))
                .sender(cardMisha)
                .user(misha)
                .recipient(cardMishaAnother)
                .build();
    }

    @Test
    void createValidTransfer() {
        Mockito.when(cardService.findCardByNumberAndUserNameForUser(transfer.getSender().getNumber(), "Misha")).thenReturn(cardMisha);
        Mockito.when(cardService.findCardByNumberAndUserNameForUser(transfer.getRecipient().getNumber(), "Misha")).thenReturn(cardMishaAnother);
        Mockito.doReturn(null).when(cardService).updateBalanceToTransfer(cardMisha, cardMishaAnother, transfer.getTransferAmount());
        Mockito.when(transferRepository.save(transfer)).thenReturn(transfer);
        Transfer saveTransfer = transferService.createTransfer(transfer, "Misha");
        Assertions.assertEquals(transfer, saveTransfer);
        Assertions.assertEquals(transfer.getSender().getNumber(), saveTransfer.getSender().getNumber());
        Assertions.assertEquals(transfer.getRecipient().getNumber(), saveTransfer.getRecipient().getNumber());
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumberAndUserNameForUser(transfer.getSender().getNumber(), "Misha");
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumberAndUserNameForUser(transfer.getRecipient().getNumber(), "Misha");
    }

    @Test
    void createInvalidTransfer() {
        cardMisha.setBalance(BigDecimal.valueOf(100));
        transfer.setTransferAmount(BigDecimal.valueOf(500));
        Mockito.when(cardService.findCardByNumberAndUserNameForUser(transfer.getSender().getNumber(), "Misha")).thenReturn(cardMisha);
        Mockito.when(cardService.findCardByNumberAndUserNameForUser(transfer.getRecipient().getNumber(), "Misha")).thenReturn(cardMishaAnother);
        Mockito.when(transferRepository.save(transfer)).thenReturn(transfer);
        Assertions.assertThrows(InvalidBalanceException.class, () -> transferService.createTransfer(transfer, "Misha"));
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumberAndUserNameForUser(transfer.getSender().getNumber(), "Misha");
        Mockito.verify(cardService, Mockito.times(1)).findCardByNumberAndUserNameForUser(transfer.getRecipient().getNumber(), "Misha");
    }
}
