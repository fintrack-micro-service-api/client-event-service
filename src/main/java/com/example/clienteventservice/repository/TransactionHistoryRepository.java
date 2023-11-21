package com.example.clienteventservice.repository;

import com.example.clienteventservice.domain.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * transaction_history of bank_account table
 */
@Transactional
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    @Query("SELECT th FROM TransactionHistory th WHERE th.bankAccountNumber = :accountNumber AND th.status = 'SUCCESS'")
    List<TransactionHistory> findByBankAccountNumberAndStatus(@Param("accountNumber") String accountNumber);

}
