package com.walletapp.project.repository.mappers;

import com.walletapp.project.enums.TransactionStatus;
import com.walletapp.project.model.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface TransactionDbMapper {

    @Select("SELECT * FROM transactions WHERE id = #{id}")
    Optional<Transaction> findById(@Param("id") UUID id);

    @Select("SELECT * FROM transactions WHERE request_id = #{requestId}")
    Optional<Transaction> findByRequestId(@Param("requestId") UUID requestId);

    @Select("SELECT * FROM transactions")
    List<Transaction> findAll();

    @Insert("""
                INSERT INTO transactions (
                    id, wallet_id, type, amount, balance_after_transaction, status, request_id, created_at, updated_at
                ) VALUES (
                    #{id}, #{walletId}, #{type}, #{amount}, #{balanceAfterTransaction},
                    #{status}, #{requestId}, #{createdAt}, #{updatedAt}
                )
            """)
    int insert(Transaction transaction);

    @Update("""
                UPDATE transactions 
                SET status = #{status}, updated_at = CURRENT_TIMESTAMP 
                WHERE id = #{transactionId}
            """)
    int updateStatus(@Param("transactionId") UUID transactionId,
                      @Param("status") TransactionStatus status);

    @Update("""
                UPDATE transactions
                SET 
                    wallet_id = #{walletId},
                    type = #{type},
                    amount = #{amount},
                    balance_after_transaction = #{balanceAfterTransaction},
                    status = #{status},
                    request_id = #{requestId},
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = #{id}
            """)
    int update(Transaction transaction);

    @Select("""
                SELECT * FROM transactions
                WHERE wallet_id = #{walletId}
                  AND created_at <= #{timestamp}
                  AND status = 'SUCCESS'
                ORDER BY created_at DESC
                LIMIT 1
            """)
    Optional<Transaction> findLatestBefore(
            @Param("walletId") UUID walletId,
            @Param("timestamp") LocalDateTime timestamp
    );
}