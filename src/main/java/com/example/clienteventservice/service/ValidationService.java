package com.example.clienteventservice.service;

import com.example.clienteventservice.exception.InsufficientBalanceManagerException;
import com.example.clienteventservice.util.ValidationUtil;
import com.example.clienteventservice.domain.model.BankAccount;
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

    public void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (isNegative(amount)) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }
    }

    private boolean isNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

}
