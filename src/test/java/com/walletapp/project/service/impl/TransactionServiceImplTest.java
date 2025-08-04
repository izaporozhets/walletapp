package com.walletapp.project.service.impl;

import com.walletapp.project.dto.TransactionDto;
import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.exception.ApiException;
import com.walletapp.project.exception.ErrorCode;
import com.walletapp.project.model.Transaction;
import com.walletapp.project.model.Wallet;
import com.walletapp.project.repository.TransactionRepository;
import com.walletapp.project.repository.WalletRepository;
import com.walletapp.project.service.IsolatedTransactionWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IsolatedTransactionWriter isolatedTransactionWriter;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void getTransactionById_shouldReturnDto() {
        UUID id = UUID.randomUUID();
        Transaction tx = Transaction.builder().id(id).amount(BigDecimal.TEN).build();

        when(transactionRepository.getTransactionById(id)).thenReturn(Optional.of(tx));

        TransactionDto result = transactionService.getTransactionById(id);

        assertEquals(id, result.getId());
        assertEquals(BigDecimal.TEN, result.getAmount());
    }

    @Test
    void getTransactionById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.getTransactionById(id)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> transactionService.getTransactionById(id));
    }

    @Test
    void getAllTransactions_shouldReturnList() {
        Transaction tx1 = Transaction.builder().id(UUID.randomUUID()).amount(BigDecimal.ONE).build();
        Transaction tx2 = Transaction.builder().id(UUID.randomUUID()).amount(BigDecimal.TEN).build();

        when(transactionRepository.getAll()).thenReturn(List.of(tx1, tx2));

        List<TransactionDto> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
    }

    @Test
    void getBalanceAt_shouldReturnBalance() {
        UUID walletId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction tx = Transaction.builder().balanceAfterTransaction(BigDecimal.valueOf(500)).build();

        when(transactionRepository.getLastSuccessfulBefore(walletId, timestamp)).thenReturn(Optional.of(tx));

        BigDecimal result = transactionService.getBalanceAt(walletId, timestamp);

        assertEquals(BigDecimal.valueOf(500), result);
    }

    @Test
    void getBalanceAt_shouldThrow_whenNoTransactionFound() {
        UUID walletId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        when(transactionRepository.getLastSuccessfulBefore(walletId, timestamp)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> transactionService.getBalanceAt(walletId, timestamp));
    }

    @Test
    void deposit_shouldCreateTransactionAndUpdateWallet() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        Wallet wallet = Wallet.builder().id(walletId).balance(BigDecimal.valueOf(200)).version(1L).build();

        when(transactionRepository.getTransactionByRequestId(requestId)).thenReturn(Optional.empty());
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.updateBalance(eq(walletId), any(), eq(1L))).thenReturn(true);

        TransactionDto result = transactionService.deposit(walletId, amount, requestId);

        assertEquals(amount, result.getAmount());
        verify(isolatedTransactionWriter).createPendingTransaction(any());
        verify(transactionRepository).update(any());
    }

    @Test
    void withdraw_shouldCreateTransactionAndUpdateWallet() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(50);
        Wallet wallet = Wallet.builder().id(walletId).balance(BigDecimal.valueOf(200)).version(1L).build();

        when(transactionRepository.getTransactionByRequestId(requestId)).thenReturn(Optional.empty());
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.updateBalance(eq(walletId), any(), eq(1L))).thenReturn(true);

        TransactionDto result = transactionService.withdraw(walletId, amount, requestId);

        assertEquals(amount, result.getAmount());
        verify(isolatedTransactionWriter).createPendingTransaction(any());
        verify(transactionRepository).update(any());
    }

    @Test
    void transfer_shouldCreateTwoTransactionsAndUpdateBothWallets() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);

        Wallet fromWallet = Wallet.builder().id(fromId).balance(BigDecimal.valueOf(500)).version(1L).build();
        Wallet toWallet = Wallet.builder().id(toId).balance(BigDecimal.valueOf(300)).version(1L).build();

        when(transactionRepository.getTransactionByRequestId(requestId)).thenReturn(Optional.empty());
        when(walletRepository.findById(fromId)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findById(toId)).thenReturn(Optional.of(toWallet));
        when(walletRepository.updateBalance(eq(fromId), any(), eq(1L))).thenReturn(true);
        when(walletRepository.updateBalance(eq(toId), any(), eq(1L))).thenReturn(true);

        TransactionDto result = transactionService.transfer(fromId, toId, amount, requestId);

        assertEquals(amount, result.getAmount());
        verify(isolatedTransactionWriter, times(2)).createPendingTransaction(any());
        verify(transactionRepository, times(2)).update(any());
    }

    @Test
    void withdraw_shouldThrow_whenInsufficientFunds() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(500);
        Wallet wallet = Wallet.builder().id(walletId).balance(BigDecimal.valueOf(100)).version(1L).build();

        when(transactionRepository.getTransactionByRequestId(requestId)).thenReturn(Optional.empty());
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        ApiException ex = assertThrows(ApiException.class, () -> transactionService.withdraw(walletId, amount, requestId));
        assertEquals(ErrorCode.INSUFFICIENT_FUNDS, ex.getErrorCode());
    }

    @Test
    void transfer_shouldThrow_whenSameWallet() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);

        ApiException ex = assertThrows(ApiException.class, () -> transactionService.transfer(walletId, walletId, amount, requestId));
        assertEquals(ErrorCode.INVALID_TRANSFER_WALLET, ex.getErrorCode());
    }

    @Test
    void deposit_shouldReturnExistingTransaction_whenRequestIdAlreadyUsed() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        Transaction existingTx = Transaction.builder()
                .id(UUID.randomUUID())
                .walletId(walletId)
                .amount(amount)
                .requestId(requestId)
                .status(TransactionStatus.SUCCESS)
                .build();

        when(transactionRepository.getTransactionByRequestId(requestId)).thenReturn(Optional.of(existingTx));

        TransactionDto result = transactionService.deposit(walletId, amount, requestId);

        assertEquals(existingTx.getId(), result.getId());
        assertEquals(amount, result.getAmount());
        verify(walletRepository, never()).findById(any());
        verify(isolatedTransactionWriter, never()).createPendingTransaction(any());
    }



}