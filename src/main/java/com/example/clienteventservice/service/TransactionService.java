package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.event.TransactionHistorySaveEvent;
import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.clienteventservice.domain.response.ApiResponse;
import com.example.clienteventservice.domain.type.StatementType;
import com.example.clienteventservice.domain.type.TransactionStatus;
import com.example.clienteventservice.domain.type.TransactionType;
import com.example.clienteventservice.event.SBAEventListener;
import com.example.clienteventservice.exception.InsufficientBalanceManagerException;
import com.example.clienteventservice.repository.TransactionHistoryRepository;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Transaction management service as TRANSFER and WITHDRAW
 */
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class TransactionService {

    private static final String ERROR_CREATING_INSERTER = "Error while transaction history to executor";
    private static final Logger LOG = LogManager.getLogger(SBAEventListener.class);


    private ValidationService validationService;
    private BankAccountService bankAccountService;
    private TransactionFeeService transactionFeeService;
    private ApplicationEventPublisher applicationEventPublisher;
    private TransactionHistoryRepository transactionHistoryRepository;

    public void executeWithdraw(BankAccount bankAccount, BigDecimal amount) {
        // validate parameters
        Preconditions.checkNotNull(bankAccount, "bankAccount can not be null");
        validationService.validAmount(amount);

        TransactionHistory.TransactionHistoryBuilder transactionHistoryBuilder = getTransactionHistoryBuilder(
                TransactionType.WITHDRAW,
                StatementType.EXPENSE,
                bankAccount,
                amount);
        try {

            takeMoney(transactionHistoryBuilder, bankAccount, amount);

        } catch (InsufficientBalanceManagerException e) {
            setTransactionHistoryBuilderAsFail(transactionHistoryBuilder, TransactionStatus.INSUFFICIENT_BALANCE, e.getMessage());
            throw e;

        } catch (RuntimeException e) {
            setTransactionHistoryBuilderAsFail(transactionHistoryBuilder, TransactionStatus.FAIL, e.getMessage());
            throw e;

        } finally {
            sendTransactionHistorySaveEvent(transactionHistoryBuilder, Optional.empty());
        }
    }


    public void executeTransfer(BankAccount fromBankAccount, BankAccount toBankAccount, final BigDecimal amount) {
        // validate parameters
        Preconditions.checkNotNull(fromBankAccount, "fromBbankAccount can not be null");
        Preconditions.checkNotNull(toBankAccount, "toBankAccount can not be null");
        Preconditions.checkArgument(!Objects.equals(fromBankAccount.getId(), toBankAccount.getId()),
                "Transfer can not executed an account to the same account. bankAccountId: ",
                fromBankAccount.getId());

        // create TransactionHistoryBuilder for fromBankAccount
        TransactionHistory.TransactionHistoryBuilder fromTransactionHistoryBuilder = getTransactionHistoryTransfer(
                TransactionType.TRANSFER,
                StatementType.EXPENSE,
                fromBankAccount,
                toBankAccount.getAccountNumber(),
                amount);

        // create TransactionHistoryBuilder for toBankAccount
        TransactionHistory.TransactionHistoryBuilder toTransactionHistoryBuilder = getTransactionHistoryBuilder(
                TransactionType.TRANSFER,
                StatementType.INCOME,
                toBankAccount,
                amount);

        try {
            validationService.validAmount(amount);

            takeMoney(fromTransactionHistoryBuilder, fromBankAccount, amount);
            putMoney(toTransactionHistoryBuilder, toBankAccount, amount);

        } catch (InsufficientBalanceManagerException e) {
            setTransactionHistoryBuilderAsFail(fromTransactionHistoryBuilder, TransactionStatus.INSUFFICIENT_BALANCE, e.getMessage());
            setTransactionHistoryBuilderAsFail(toTransactionHistoryBuilder, TransactionStatus.INSUFFICIENT_BALANCE, e.getMessage());
            throw e;

        } catch (RuntimeException e) {
            setTransactionHistoryBuilderAsFail(fromTransactionHistoryBuilder, TransactionStatus.FAIL, e.getMessage());
            setTransactionHistoryBuilderAsFail(toTransactionHistoryBuilder, TransactionStatus.FAIL, e.getMessage());
            throw e;

        } finally {
            sendTransactionHistorySaveEvent(fromTransactionHistoryBuilder, Optional.of(toTransactionHistoryBuilder));
        }
    }

    public void executeDeposit(BankAccount toBankAccount, BigDecimal amount) {
        Preconditions.checkNotNull(toBankAccount, "bankAccount can not be null");
        validationService.validAmount(amount);

        TransactionHistory.TransactionHistoryBuilder transactionHistoryBuilder = getTransactionHistoryBuilder(
                TransactionType.DEPOSIT,
                StatementType.INCOME,
                toBankAccount,
                amount);
        try {
            putMoney(transactionHistoryBuilder, toBankAccount, amount);

        } catch (RuntimeException e) {
            setTransactionHistoryBuilderAsFail(transactionHistoryBuilder, TransactionStatus.FAIL, e.getMessage());
            throw e;

        } finally {
            sendTransactionHistorySaveEvent(transactionHistoryBuilder, Optional.empty());
        }
    }

    private void takeMoney(TransactionHistory.TransactionHistoryBuilder transactionHistoryBuilder, BankAccount bankAccount, BigDecimal amount) {
        BigDecimal fee = transactionFeeService.getFee(TransactionType.WITHDRAW, bankAccount, amount);
        BigDecimal totalAmount = transactionFeeService.getTotalAmount(amount, fee);

        validationService.checkWithdrawable(bankAccount, totalAmount);

        BankAccount updatedBankAccount = bankAccountService.decreaseCurrentBalance(bankAccount, totalAmount);
        validationService.validateCurrentBalance(updatedBankAccount);

        transactionHistoryBuilder.status(TransactionStatus.SUCCESS)
                .fee(fee)
                .totalAmount(totalAmount)
                .afterBalance(updatedBankAccount.getCurrentBalance());
    }

    private void putMoney(TransactionHistory.TransactionHistoryBuilder transactionHistoryBuilder, BankAccount bankAccount, BigDecimal amount) {

        BankAccount updatedBankAccount = bankAccountService.increaseCurrentBalance(bankAccount, amount);

        transactionHistoryBuilder.status(TransactionStatus.SUCCESS)
                .fee(BigDecimal.ZERO)
                .totalAmount(amount)
                .afterBalance(updatedBankAccount.getCurrentBalance());
    }

    public TransactionHistory.TransactionHistoryBuilder getTransactionHistoryBuilder(
            TransactionType transactionType,
            StatementType statementType,
            BankAccount bankAccount,
            BigDecimal amount) {

        return TransactionHistory.builder()
                .type(transactionType)
                .statementType(statementType)
                .amount(amount)
                .customerId(bankAccount.getCustomerId())
                .bankAccountNumber(bankAccount.getAccountNumber())
//                .cardId(bankAccount.getCard().getId())
                .beforeBalance(bankAccount.getCurrentBalance());
    }


    //use for transfer events to push to kafka topic
    public TransactionHistory.TransactionHistoryBuilder getTransactionHistoryTransfer(
            TransactionType transactionType,
            StatementType statementType,
            BankAccount senderBankAccountNumber,
            String receivedBankAccountNumber,
            BigDecimal amount) {

        return TransactionHistory.builder()
                .type(transactionType)
                .statementType(statementType)
                .bankAccountNumber(senderBankAccountNumber.getAccountNumber())
                .receivedAccountNumber(receivedBankAccountNumber)
                .amount(amount)
                .customerId(senderBankAccountNumber.getCustomerId())
                .beforeBalance(senderBankAccountNumber.getCurrentBalance());
    }

    private void setTransactionHistoryBuilderAsFail(
            TransactionHistory.TransactionHistoryBuilder transactionHistoryBuilder,
            TransactionStatus transactionStatus,
            String failingReason) {

        transactionHistoryBuilder.status(transactionStatus)
                .failingReason(failingReason);
    }

    private void sendTransactionHistorySaveEvent(TransactionHistory.TransactionHistoryBuilder fromTransactionHistoryBuilder,
                                                 Optional<TransactionHistory.TransactionHistoryBuilder> toTransactionHistoryBuilderOptional) {

        TransactionHistorySaveEvent.TransactionHistorySaveEventBuilder builder = TransactionHistorySaveEvent.builder()
                .fromTransactionHistory(fromTransactionHistoryBuilder.build())
                .eventSource(getClass().getName());

        toTransactionHistoryBuilderOptional.ifPresent(toTransactionHistoryBuilder ->
                builder.toTransactionHistory(toTransactionHistoryBuilder.build()));

        sendTransactionHistorySaveEvent(builder.build());
    }

    private void sendTransactionHistorySaveEvent(TransactionHistorySaveEvent transactionHistorySaveEvent) {
        try {
            applicationEventPublisher.publishEvent(transactionHistorySaveEvent);
        } catch (Exception e) {
            LOG.error(ERROR_CREATING_INSERTER, e);
        }
    }

    public ApiResponse<List<TransactionHistory>> getTransactionHistoryByAccountNumber(String bankAccountNumber) {
        try {
            List<TransactionHistory> transactionHistoryList = transactionHistoryRepository.findByBankAccountNumberAndStatus(bankAccountNumber);

            if (!transactionHistoryList.isEmpty()) {
                return ApiResponse.<List<TransactionHistory>>builder()
                        .message("Transaction history retrieved successfully")
                        .status(HttpStatus.OK.value())
                        .payload(transactionHistoryList)
                        .build();
            } else {
                return ApiResponse.<List<TransactionHistory>>builder()
                        .message("No transaction history found for the given bank account number")
                        .status(HttpStatus.NOT_FOUND.value())
                        .build();
            }
        } catch (Exception e) {
            return ApiResponse.<List<TransactionHistory>>builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

}
