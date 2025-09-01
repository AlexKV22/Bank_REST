package com.example.bankcards.controller;

import com.example.bankcards.dto.dtoRequest.TransferRequestDto;
import com.example.bankcards.dto.dtoResponse.TransferResponseDto;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.service.transferService.TransferService;
import com.example.bankcards.dto.util.TransferServiceDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user/transfer")
public class TransferController {

    private final TransferService transferService;
    private final TransferServiceDto transferServiceDto;

    @Autowired
    public TransferController(TransferService transferService, TransferServiceDto transferServiceDto) {
        this.transferService = transferService;
        this.transferServiceDto = transferServiceDto;
    }

    @PostMapping
    public ResponseEntity<TransferResponseDto> createTransfer(@Valid @RequestBody TransferRequestDto transferRequestDto,
                                              Principal principal) {
        Transfer completeTransfer = transferService.createTransfer(transferServiceDto.dtoToEntity(transferRequestDto), principal.getName());
        return ResponseEntity.ok(transferServiceDto.entityToDto(completeTransfer));
    }
}
