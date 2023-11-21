package com.example.clienteventservice.service;

import com.example.clienteventservice.exception.InsufficientBalanceManagerException;
import com.example.clienteventservice.util.ValidationUtil;
import com.example.clienteventservice.domain.model.BankAccount;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Validation service
 */
@Slf4j
@Service
public class ValidationService {

    public void checkWithdrawable(BankAccount bankAccount, BigDecimal amount) {
        if (ValidationUtil.isNegative(bankAccount.getCurrentBalance().subtract(amount))) {
            throw InsufficientBalanceManagerException.to(
                    "Account current balance is not available to withdraw. current balance: %s, amount: %s",
                    bankAccount.getCurrentBalance(),
                    amount);
        }
    }

    public void validateCurrentBalance(BankAccount bankAccount) {
        if (ValidationUtil.isNegative(bankAccount.getCurrentBalance())) {
            throw InsufficientBalanceManagerException.to(
                    "Account current balance is not available to withdraw/transfer. current balance: %s",
                    bankAccount.getCurrentBalance());
        }
    }

    public void validAmount(BigDecimal amount) {
        Preconditions.checkNotNull(amount, "amount can not be null");
        Preconditions.checkArgument(!ValidationUtil.isNegative(amount), "amount can not be negative");
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) != 0, "amount can not be zero");
    }

}
