package com.walletapp.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@AllArgsConstructor
public class WalletDto {

    private UUID id;

    private BigDecimal balance;
}