package com.walletapp.project.dto;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class TransactionDto {

    private UUID id;

    private UUID requestId;

    private UUID walletId;

    private TransactionType type;

    private BigDecimal amount;

    private BigDecimal balanceAfterTransaction;

    private TransactionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}