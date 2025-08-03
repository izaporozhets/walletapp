package com.walletapp.project.repository;

import com.walletapp.project.model.Wallet;
import com.walletapp.project.repository.mappers.WalletDbMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletRepository {

    private final WalletDbMapper walletMapper;

    public Optional<Wallet> findById(UUID id) {
        return walletMapper.findById(id);
    }

    public List<Wallet> findAll() {
        return walletMapper.findAll();
    }

    public boolean updateBalance(UUID id, BigDecimal balance, Long version) {
        return walletMapper.updateBalanceIfVersionMatch(id, balance, version) == 1;
    }
}
