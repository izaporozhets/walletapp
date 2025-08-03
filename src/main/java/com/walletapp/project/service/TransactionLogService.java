package com.walletapp.project.service;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public interface TransactionLogService {

    void createPendingTransaction(Transaction tx);

    void markSuccess(UUID transactionId, BigDecimal newBalance);

    void markFailed(UUID transactionId);

    void updateStatus(UUID transactionId, TransactionStatus status);
}