package com.walletapp.project.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    private UUID id;

    private BigDecimal balance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
