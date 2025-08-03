package com.walletapp.project.mapper;

import com.walletapp.project.dto.WalletDto;
import com.walletapp.project.model.Wallet;

public class WalletDtoMapper {

    public static WalletDto toDto(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        return new WalletDto(wallet.getId(), wallet.getBalance());
    }
}
