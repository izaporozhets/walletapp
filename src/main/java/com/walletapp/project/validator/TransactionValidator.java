package com.walletapp.project.validator;

import com.walletapp.project.exception.ApiException;
import com.walletapp.project.exception.ErrorCode;

import java.math.BigDecimal;
import java.util.UUID;

public final class TransactionValidator {

    private TransactionValidator() { }

    public static void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(ErrorCode.NEGATIVE_AMOUNT);
        }
    }

    public static void validateNotSameWallet(UUID fromWalletId, UUID toWalletId) {
        if (fromWalletId.equals(toWalletId)) {
            throw new ApiException(ErrorCode.INVALID_TRANSFER_WALLET);
        }
    }

    public static void validateSufficientFunds(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new ApiException(ErrorCode.INSUFFICIENT_FUNDS);
        }
    }

}