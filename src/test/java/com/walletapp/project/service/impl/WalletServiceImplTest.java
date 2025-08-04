package com.walletapp.project.service.impl;

import com.walletapp.project.dto.WalletDto;
import com.walletapp.project.exception.ApiException;
import com.walletapp.project.exception.ErrorCode;
import com.walletapp.project.model.Wallet;
import com.walletapp.project.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void getAllWallets_shouldReturnListOfDtos() {
        Wallet wallet1 = Wallet.builder().id(UUID.randomUUID()).balance(BigDecimal.valueOf(100)).build();
        Wallet wallet2 = Wallet.builder().id(UUID.randomUUID()).balance(BigDecimal.valueOf(200)).build();

        when(walletRepository.findAll()).thenReturn(List.of(wallet1, wallet2));

        List<WalletDto> result = walletService.getAllWallets();

        assertEquals(2, result.size());
        assertEquals(wallet1.getId(), result.get(0).getId());
        assertEquals(wallet2.getBalance(), result.get(1).getBalance());
    }

    @Test
    void getWalletById_shouldReturnDto_whenWalletExists() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(walletId).balance(BigDecimal.valueOf(300)).build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        WalletDto result = walletService.getWalletById(walletId);

        assertEquals(walletId, result.getId());
        assertEquals(wallet.getBalance(), result.getBalance());
    }

    @Test
    void getWalletById_shouldThrow_whenWalletNotFound() {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> walletService.getWalletById(walletId));
        assertEquals(ErrorCode.WALLET_NOT_FOUND, ex.getErrorCode());
    }
}