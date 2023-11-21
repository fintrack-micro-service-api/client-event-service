package com.example.clienteventservice.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * bank_account table java object
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_account")
@EntityListeners(AuditingEntityListener.class)
public class BankAccount {
    @Id
    @GenericGenerator(name = "bank_account_generator", strategy = "increment")
    @GeneratedValue(generator = "bank_account_generator")
    private Long id;

    private String accountNumber;

    @NonNull
    private BigDecimal currentBalance;

//    @OneToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id")
//    private Customer customer;
    private UUID customerId;


    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public BankAccount(String accountNumber, @NonNull BigDecimal currentBalance) {
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
    }



    //    @OneToOne(optional = false, cascade = CascadeType.ALL, mappedBy = "bankAccount", fetch = FetchType.LAZY)
//    private Card card;

}
