package com.example.clienteventservice.domain.model;
import com.example.clienteventservice.domain.dto.TransactionHistoryDto;
import com.example.clienteventservice.domain.type.StatementType;
import com.example.clienteventservice.domain.type.TransactionStatus;
import com.example.clienteventservice.domain.type.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * transaction_history table java object
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_history")
@EntityListeners(AuditingEntityListener.class)
public class TransactionHistory implements Serializable {

    @Id
    @GenericGenerator(name = "transaction_generator", strategy = "increment")
    @GeneratedValue(generator = "transaction_generator")
    private Long id;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    @Enumerated(EnumType.STRING)
    private StatementType statementType;

    private UUID customerId;

    private String bankAccountNumber;

    private String receivedAccountNumber;

//    private Long cardId;

    private BigDecimal amount;

    private BigDecimal fee;

    private BigDecimal totalAmount;

    private BigDecimal beforeBalance;

    private BigDecimal afterBalance;

    private Long correlationId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String failingReason;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public TransactionHistoryDto toDto() {
        return new TransactionHistoryDto(
                this.id,
                this.type,
                this.statementType,
                this.customerId,
                this.bankAccountNumber,
                this.receivedAccountNumber,
                this.amount,
                this.fee,
                this.totalAmount,
                this.beforeBalance,
                this.afterBalance,
                this.correlationId,
                this.status,
                this.failingReason,
                this.createdAt,
                this.updatedAt
        );
    }


}
