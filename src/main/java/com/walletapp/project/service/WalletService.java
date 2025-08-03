package com.walletapp.project.service;

import com.walletapp.project.dto.WalletDto;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    List<WalletDto> getAllWallets();

    WalletDto getWalletById(UUID id);
}
