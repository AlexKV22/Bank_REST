package com.example.bankcards.service.transferService;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.InvalidBalanceException;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.service.cardService.CardServiceImpl;
import com.example.bankcards.service.userService.UserService;
import com.example.bankcards.util.TransferStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TransferServiceImpl implements TransferService {
    private final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
    private final TransferRepository transferRepository;
    private final CardService cardService;

    @Autowired
    public TransferServiceImpl(TransferRepository transferRepository, CardService cardService) {
        this.transferRepository = transferRepository;
        this.cardService = cardService;
    }

    @Override
    @Transactional
    public Transfer createTransfer(Transfer transfer, String name) {
        Card cardFrom = cardService.findCardByNumberAndUserNameForUser(transfer.getSender().getNumber(), name);
        Card cardTo = cardService.findCardByNumberAndUserNameForUser(transfer.getRecipient().getNumber(), name);
        logger.info("Успешно получены карты отправителя и получателя в сервис создания перевода.");
            if (transfer.getTransferAmount().compareTo(cardFrom.getBalance()) <= 0) {
                cardService.updateBalanceToTransfer(cardFrom, cardTo, transfer.getTransferAmount());
                logger.info("Успешно сохранены измененные суммы карт отправителя и получателя в сервисе карт.");
                transfer.setSender(cardFrom);
                transfer.setRecipient(cardTo);
                transfer.setUser(cardFrom.getUser());
                transfer.setTransferDate(LocalDate.now());
                transfer.setTransferStatus(TransferStatus.SUCCESS);
                return transferRepository.save(transfer);
            } else {
                transfer.setTransferStatus(TransferStatus.FAILED);
                transferRepository.save(transfer);
                throw new InvalidBalanceException(cardFrom.getNumber());
            }
    }
}
