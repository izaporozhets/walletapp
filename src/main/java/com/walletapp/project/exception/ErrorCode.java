package com.walletapp.project.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    WALLET_NOT_FOUND("10001", "Wallet not found"),
    INSUFFICIENT_BALANCE("10002", "Insufficient balance"),
    TRANSACTION_NOT_FOUND("10003", "Transaction not found"),
    DB_TRANSACTION_FAILURE("10004", "Database transaction failure"),
    NEGATIVE_AMOUNT("10005", "Amount must be greater than 0"),
    INVALID_WALLET_UPDATE("10006", "Invalid wallet update"),
    INSUFFICIENT_FUNDS("10007", "Insufficient funds"),
    INVALID_TRANSFER_WALLET("10008", "Source and destination wallets must be different"),
    INVALID_TRANSACTION_TYPE("10009", "Invalid transaction type");

    private final String statusCode;
    private final String message;

    ErrorCode(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
