package com.walletapp.project.repository.mappers;

import com.walletapp.project.model.Wallet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface WalletDbMapper {

    @Select("""
                SELECT id, balance, created_at, updated_at, version
                FROM wallets
                WHERE id = #{id}
            """)
    Optional<Wallet> findById(@Param("id") UUID id);

    @Select("SELECT * FROM wallets")
    List<Wallet> findAll();

    @Insert("INSERT INTO wallets (id, balance, created_at, updated_at) VALUES (#{id}, #{balance}, #{createdAt}, #{updatedAt})")
    void insert(Wallet wallet);

    @Update("""
                UPDATE wallets
                SET balance = #{balance},
                    updated_at = CURRENT_TIMESTAMP,
                    version = version + 1
                WHERE id = #{id}
                  AND version = #{version}
            """)
    int updateBalanceIfVersionMatch(@Param("id") UUID id, @Param("balance") BigDecimal balance, @Param("version") Long version);

}