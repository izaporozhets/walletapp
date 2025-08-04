package com.walletapp.project.service.impl;

import com.walletapp.project.dto.TransactionDto;
import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.enums.TransactionType;
import com.walletapp.project.exception.ApiException;
import com.walletapp.project.exception.ErrorCode;
import com.walletapp.project.mapper.TransactionDtoMapper;
import com.walletapp.project.model.Transaction;
import com.walletapp.project.model.Wallet;
import com.walletapp.project.repository.TransactionRepository;
import com.walletapp.project.repository.WalletRepository;
import com.walletapp.project.service.IsolatedTransactionWriter;
import com.walletapp.project.service.TransactionService;
import com.walletapp.project.validator.TransactionValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IsolatedTransactionWriter isolatedTransactionWriter;

    @Override
    @Transactional(readOnly = true)
    public TransactionDto getTransactionById(UUID id) {
        return transactionRepository.getTransactionById(id)
                .map(TransactionDtoMapper::toDto)
                .orElseThrow(() -> new ApiException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.getAll().stream()
                .map(TransactionDtoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalanceAt(UUID walletId, LocalDateTime timestamp) {
        return transactionRepository
                .getLastSuccessfulBefore(walletId, timestamp)
                .map(Transaction::getBalanceAfterTransaction)
                .orElseThrow(() -> new ApiException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    @Override
    @Transactional
    public TransactionDto deposit(UUID walletId, BigDecimal amount, UUID requestId) {
        TransactionValidator.validatePositiveAmount(amount);
        return getExistingTransaction(requestId).orElseGet(() -> {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND));
            Transaction tx = buildTransaction(wallet, amount, requestId, TransactionType.DEPOSIT);
            isolatedTransactionWriter.createPendingTransaction(tx);

            BigDecimal newBalance = wallet.getBalance().add(amount);
            updateWalletBalanceOrFail(wallet.getId(), newBalance, wallet.getVersion(), tx.getId());

            finalizeTransaction(tx, newBalance);
            return TransactionDtoMapper.toDto(tx);
        });
    }

    @Override
    @Transactional
    public TransactionDto withdraw(UUID walletId, BigDecimal amount, UUID requestId) {
        TransactionValidator.validatePositiveAmount(amount);

        return getExistingTransaction(requestId).orElseGet(() -> {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND));
            TransactionValidator.validateSufficientFunds(wallet.getBalance(), amount);

            Transaction tx = buildTransaction(wallet, amount, requestId, TransactionType.WITHDRAWAL);
            isolatedTransactionWriter.createPendingTransaction(tx);

            BigDecimal newBalance = wallet.getBalance().subtract(amount);
            updateWalletBalanceOrFail(wallet.getId(), newBalance, wallet.getVersion(), tx.getId());

            finalizeTransaction(tx, newBalance);
            return TransactionDtoMapper.toDto(tx);
        });
    }

    @Override
    @Transactional
    public TransactionDto transfer(UUID fromWalletId, UUID toWalletId, BigDecimal amount, UUID requestId) {
        TransactionValidator.validatePositiveAmount(amount);
        TransactionValidator.validateNotSameWallet(fromWalletId, toWalletId);

        return getExistingTransaction(requestId).orElseGet(() -> {
            Wallet fromWallet = walletRepository.findById(fromWalletId)
                    .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND));
            Wallet toWallet = walletRepository.findById(toWalletId)
                    .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND));
            TransactionValidator.validateSufficientFunds(fromWallet.getBalance(), amount);

            Transaction outTx = buildTransaction(fromWallet, amount, requestId, TransactionType.TRANSFER_OUT);
            Transaction inTx = buildTransaction(toWallet, amount, null, TransactionType.TRANSFER_IN);
            isolatedTransactionWriter.createPendingTransaction(outTx);
            isolatedTransactionWriter.createPendingTransaction(inTx);

            BigDecimal newFromBalance = fromWallet.getBalance().subtract(amount);
            updateWalletBalanceOrFail(fromWallet.getId(), newFromBalance, fromWallet.getVersion(), outTx.getId());

            BigDecimal newToBalance = toWallet.getBalance().add(amount);
            updateWalletBalanceOrFail(toWallet.getId(), newToBalance, toWallet.getVersion(), inTx.getId());

            finalizeTransaction(outTx, newFromBalance);
            finalizeTransaction(inTx, newToBalance);

            return TransactionDtoMapper.toDto(outTx);
        });
    }


    private Optional<TransactionDto> getExistingTransaction(UUID requestId) {
        Optional<Transaction> transaction = transactionRepository.getTransactionByRequestId(requestId);
        return transaction.map(TransactionDtoMapper::toDto);
    }


    private void updateWalletBalanceOrFail(UUID walletId, BigDecimal newBalance, long version, UUID txId) {
        boolean updated = walletRepository.updateBalance(walletId, newBalance, version);
        if (!updated) {
            isolatedTransactionWriter.updateStatus(txId, TransactionStatus.FAILED);
            throw new ApiException(ErrorCode.INVALID_WALLET_UPDATE);
        }
    }

    private void finalizeTransaction(Transaction tx, BigDecimal newBalance) {
        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setBalanceAfterTransaction(newBalance);
        tx.setUpdatedAt(LocalDateTime.now());
        transactionRepository.update(tx);
    }

    private Transaction buildTransaction(Wallet wallet, BigDecimal amount, UUID requestId, TransactionType type) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .walletId(wallet.getId())
                .amount(amount)
                .type(type)
                .balanceAfterTransaction(wallet.getBalance())
                .status(TransactionStatus.PENDING)
                .requestId(requestId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}