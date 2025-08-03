package com.walletapp.project.mapper;

import com.walletapp.project.dto.TransactionDto;
import com.walletapp.project.model.Transaction;

public class TransactionDtoMapper {
    public static TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionDto.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balanceAfterTransaction(transaction.getBalanceAfterTransaction())
                .status(transaction.getStatus())
                .requestId(transaction.getRequestId())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}