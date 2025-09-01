package com.example.bankcards.service.transferService;

import com.example.bankcards.entity.Transfer;

public interface TransferService {
    Transfer createTransfer(Transfer transfer, String name);
}
