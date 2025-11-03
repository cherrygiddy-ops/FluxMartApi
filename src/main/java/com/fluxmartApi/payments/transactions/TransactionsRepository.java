package com.fluxmartApi.payments.transactions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TransactionsRepository extends JpaRepository<TransactionEntity,String> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t")
    BigDecimal findTotalAmountAcrossAllTransactions();
}
