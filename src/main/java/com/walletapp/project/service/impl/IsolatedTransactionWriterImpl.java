package com.walletapp.project.service.impl;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.model.Transaction;
import com.walletapp.project.repository.TransactionRepository;
import com.walletapp.project.service.IsolatedTransactionWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IsolatedTransactionWriterImpl implements IsolatedTransactionWriter {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createPendingTransaction(Transaction tx) {
        transactionRepository.insert(tx);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(UUID transactionId, TransactionStatus status) {
        transactionRepository.updateStatus(transactionId, status);
    }
}