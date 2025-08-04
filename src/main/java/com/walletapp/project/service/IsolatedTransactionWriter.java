package com.walletapp.project.service;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface IsolatedTransactionWriter {

    void createPendingTransaction(Transaction tx);

    void updateStatus(UUID transactionId, TransactionStatus status);
}