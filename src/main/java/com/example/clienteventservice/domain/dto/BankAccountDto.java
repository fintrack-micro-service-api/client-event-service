package com.example.clienteventservice.domain.dto;

import com.example.clienteventservice.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * This class is an integration class for rest services
 */
@Builder
@Data
// we should add these two annotations if we use builder for DTOs
// Fixing the errors: no Creators, like default construct, exist
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {

    private String accountNumber;
    private BigDecimal balance;

//    private CardDto card;

    public BankAccount toEntity(){
        return new BankAccount(accountNumber, balance);
    }

}
