package com.walletapp.project.service;

import com.walletapp.project.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionDto getTransactionById(UUID id);

    List<TransactionDto> getAllTransactions();

    TransactionDto deposit(UUID walletId, BigDecimal amount, UUID requestId);

    TransactionDto withdraw(UUID walletId, BigDecimal amount, UUID requestId);

    TransactionDto transfer(UUID fromWalletId, UUID toWalletId, BigDecimal amount, UUID requestId);
}
