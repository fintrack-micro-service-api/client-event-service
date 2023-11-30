package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.dto.TransactionHistoryDto;
import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.clienteventservice.domain.response.ApiResponse;
import com.example.clienteventservice.domain.type.StatementType;
import com.example.clienteventservice.domain.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Withdraw process management service
 */
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class WithdrawService {

    private TransactionService transactionService;
    private BankAccountService bankAccountService;

    private final KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate;

    public ApiResponse<Void> withdraw(String bankAccountNumber, BigDecimal amount) {
        try {
            ApiResponse<BankAccount> bankAccountResponse = bankAccountService.getBankAccount(bankAccountNumber);

            if (bankAccountResponse.getStatus() == HttpStatus.NOT_FOUND.value()) {
                return ApiResponse.<Void>builder()
                        .message("Bank account not found")
                        .status(HttpStatus.NOT_FOUND.value())
                        .build();
            }

            BankAccount bankAccount = bankAccountResponse.getPayload();
            transactionService.executeWithdraw(bankAccount, amount);

            TransactionHistory transactionHistory = transactionService
                    .getTransactionHistoryBuilder(
                            TransactionType.WITHDRAW,
                            StatementType.EXPENSE,
                            bankAccount,
                            amount
                    ).build();

            Message<TransactionHistoryDto> message = MessageBuilder
                    .withPayload(transactionHistory.toDto())
                    .setHeader(KafkaHeaders.TOPIC, "notification-alert-service")
                    .build();
            kafkaTemplate.send(message);

            return ApiResponse.<Void>builder()
                    .message("Withdrawal successful")
                    .status(HttpStatus.OK.value())
                    .build();
        } catch (IllegalArgumentException e) {
            return ApiResponse.<Void>builder()
                    .message("Invalid amount: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

}
