package com.example.bankcards.dto.converter;

import com.example.bankcards.dto.dtoRequest.TransferRequestDto;
import com.example.bankcards.dto.dtoResponse.TransferResponseDto;
import com.example.bankcards.entity.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CardMapper.class)
public interface TransferMapper {
    @Mapping(target = "transferDate", source = "transferDate")
    @Mapping(target = "transferAmount", source = "transferAmount")
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "recipient", source = "recipient")
    @Mapping(target = "transferStatus", source = "transferStatus")
    TransferResponseDto entityToDto(Transfer transfer);


    @Mapping(target = "transferAmount", source = "amount")
    @Mapping(target = "sender", source = "cardSender")
    @Mapping(target = "recipient", source = "cardRecipient")
    Transfer dtoToEntity(TransferRequestDto transferRequestDtoDto);
}
