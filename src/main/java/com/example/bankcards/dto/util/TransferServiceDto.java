package com.example.bankcards.dto.util;

import com.example.bankcards.dto.converter.TransferMapper;
import com.example.bankcards.dto.dtoRequest.TransferRequestDto;
import com.example.bankcards.dto.dtoResponse.TransferResponseDto;
import com.example.bankcards.entity.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferServiceDto {

    private final TransferMapper transferMapper;

    @Autowired
    public TransferServiceDto(TransferMapper transferMapper) {
        this.transferMapper = transferMapper;
    }

    public TransferResponseDto entityToDto(Transfer transfer) {
        return transferMapper.entityToDto(transfer);
    }

    public Transfer dtoToEntity(TransferRequestDto transferRequestDto) {
        return transferMapper.dtoToEntity(transferRequestDto);
    }
}
