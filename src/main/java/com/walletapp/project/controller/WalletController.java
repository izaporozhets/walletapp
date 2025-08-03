package com.walletapp.project.controller;

import com.walletapp.project.dto.TransactionActionRequest;
import com.walletapp.project.dto.TransactionDto;
import com.walletapp.project.dto.WalletDto;
import com.walletapp.project.service.TransactionService;
import com.walletapp.project.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController()
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<WalletDto> getWallet(@PathVariable UUID id) {
        return ResponseEntity.ok(walletService.getWalletById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<WalletDto>> getWallet() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> deposit(@RequestBody TransactionActionRequest request) {
        TransactionDto transactionDto = transactionService.deposit(request.getWalletId(), request.getAmount(), request.getRequestId());
        return ResponseEntity.ok(transactionDto);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDto> withdraw(@RequestBody TransactionActionRequest request) {
        TransactionDto transactionDto = transactionService.withdraw(request.getWalletId(), request.getAmount(), request.getRequestId());
        return ResponseEntity.ok(transactionDto);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDto> transfer(@RequestBody TransactionActionRequest request) {
        TransactionDto transactionDto = transactionService.transfer(request.getFromWalletId(), request.getToWalletId(),
                request.getAmount(), request.getRequestId());
        return ResponseEntity.ok(transactionDto);
    }
}