package com.example.clienteventservice.converter;

import com.example.clienteventservice.domain.dto.BalanceDto;
import com.example.clienteventservice.domain.model.BankAccount;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * BankAccount to BalanceDto converter
 */
@Component
public class BankAccountToBalanceDtoConverter implements Converter<BankAccount, BalanceDto> {

    @Override
    public BalanceDto convert(BankAccount bankAccount) {
        return BalanceDto.builder()
                .bankAccountNumber(bankAccount.getAccountNumber())
                .currentBalance(bankAccount.getCurrentBalance())
                .build();
    }

}
