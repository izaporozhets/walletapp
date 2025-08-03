package com.walletapp.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionActionRequest {
    private UUID walletId;
    private UUID fromWalletId;
    private UUID toWalletId;
    private BigDecimal amount;
    private UUID requestId;
}