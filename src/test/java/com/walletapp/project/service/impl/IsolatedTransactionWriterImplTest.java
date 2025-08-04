package com.walletapp.project.service.impl;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.model.Transaction;
import com.walletapp.project.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IsolatedTransactionWriterImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private IsolatedTransactionWriterImpl isolatedTransactionWriter;

    @Test
    void createPendingTransaction_shouldCallInsert() {
        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .walletId(UUID.randomUUID())
                .amount(java.math.BigDecimal.TEN)
                .status(TransactionStatus.PENDING)
                .build();

        isolatedTransactionWriter.createPendingTransaction(tx);

        verify(transactionRepository).insert(tx);
    }

    @Test
    void updateStatus_shouldCallUpdateStatus() {
        UUID transactionId = UUID.randomUUID();
        TransactionStatus status = TransactionStatus.SUCCESS;

        isolatedTransactionWriter.updateStatus(transactionId, status);

        verify(transactionRepository).updateStatus(transactionId, status);
    }
}