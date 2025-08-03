package com.walletapp.project.repository;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.exception.ApiException;
import com.walletapp.project.exception.ErrorCode;
import com.walletapp.project.model.Transaction;
import com.walletapp.project.repository.mappers.TransactionDbMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {

    private final TransactionDbMapper transactionMapper;

    public Optional<Transaction> getTransactionById(UUID id) {
        return transactionMapper.findById(id);
    }

    public Optional<Transaction> getTransactionByRequestId(UUID id) {
        Optional<Transaction> transaction = transactionMapper.findByRequestId(id);
        return transaction;
    }

    public List<Transaction> getAll() {
        return transactionMapper.findAll();
    }

    public void updateStatus(UUID transactionId, TransactionStatus status) {
        int affected = transactionMapper.updateStatus(transactionId, status);
        if (affected != 1) {
            throw new ApiException(ErrorCode.DB_TRANSACTION_FAILURE);
        }
    }

    public void insert(Transaction transaction) {
        int affected = transactionMapper.insert(transaction);
        if (affected != 1) {
            throw new ApiException(ErrorCode.DB_TRANSACTION_FAILURE);
        }
    }

    public void update(Transaction transaction) {
        int affected = transactionMapper.update(transaction);
        if (affected != 1) {
            throw new ApiException(ErrorCode.DB_TRANSACTION_FAILURE);
        }
    }

}
