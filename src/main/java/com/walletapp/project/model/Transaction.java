package com.walletapp.project.model;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    private UUID id;

    private UUID walletId;

    private TransactionType type;

    private BigDecimal amount;

    private BigDecimal balanceAfterTransaction;

    private TransactionStatus status;

    private UUID requestId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}