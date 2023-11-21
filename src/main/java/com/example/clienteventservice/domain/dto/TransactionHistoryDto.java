package com.example.clienteventservice.domain.dto;

import com.example.clienteventservice.domain.type.StatementType;
import com.example.clienteventservice.domain.type.TransactionStatus;
import com.example.clienteventservice.domain.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionHistoryDto {

    private Long id;

    private TransactionType type;

    private StatementType statementType;

    private UUID customerId;

    private String bankAccountNumber;

    private String receivedAccountNumber;

    private BigDecimal amount;

    private BigDecimal fee;

    private BigDecimal totalAmount;

    private BigDecimal beforeBalance;

    private BigDecimal afterBalance;

    private Long correlationId;

    private TransactionStatus status;

    private String failingReason;

    private Date createdAt;

    private Date updatedAt;
}
