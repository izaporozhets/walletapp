package com.walletapp.project.service.impl;

import com.walletapp.project.dto.WalletDto;
import com.walletapp.project.exception.ApiException;
import com.walletapp.project.exception.ErrorCode;
import com.walletapp.project.mapper.WalletDtoMapper;
import com.walletapp.project.repository.WalletRepository;
import com.walletapp.project.model.Wallet;
import com.walletapp.project.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WalletDto> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(WalletDtoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WalletDto getWalletById(UUID id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND));
        return WalletDtoMapper.toDto(wallet);
    }
}