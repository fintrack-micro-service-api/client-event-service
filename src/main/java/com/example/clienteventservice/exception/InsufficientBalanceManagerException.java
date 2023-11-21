package com.example.clienteventservice.exception;

/**
 * The exception to manage unabalible balance cases
 */
public class InsufficientBalanceManagerException extends BankAccountManagerException {

    private InsufficientBalanceManagerException(String format, Object... parameters) {
        super(format, parameters);
    }

    public static InsufficientBalanceManagerException to(String format, Object... parameters) {
        return new InsufficientBalanceManagerException(format, parameters);
    }

}
