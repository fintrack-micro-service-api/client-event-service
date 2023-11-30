package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.dto.TransactionHistoryDto;
import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.clienteventservice.domain.response.ApiResponse;
import com.example.clienteventservice.domain.type.StatementType;
import com.example.clienteventservice.domain.type.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class DepositService {
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    private final KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate;

    public ApiResponse<Void> deposit(String bankAccountNumber, BigDecimal amount) {
        try {
            ApiResponse<BankAccount> response = bankAccountService.getBankAccount(bankAccountNumber);

            if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
                return ApiResponse.<Void>builder()
                        .message("Bank account not found")
                        .status(HttpStatus.NOT_FOUND.value())
                        .build();
            }

            BankAccount toBankAccount = response.getPayload();
            transactionService.executeDeposit(toBankAccount, amount);

            TransactionHistory transactionHistory = transactionService
                    .getTransactionHistoryBuilder(
                            TransactionType.DEPOSIT,
                            StatementType.INCOME,
                            toBankAccount,
                            amount
                    ).build();

            Message<TransactionHistoryDto> message = MessageBuilder
                    .withPayload(transactionHistory.toDto())
                    .setHeader(KafkaHeaders.TOPIC, "notification-alert-service")
                    .build();
            kafkaTemplate.send(message);

            return ApiResponse.<Void>builder()
                    .message("Deposit successful")
                    .status(HttpStatus.OK.value())
                    .build();
        } catch (IllegalArgumentException e) {
            return ApiResponse.<Void>builder()
                    .message("Invalid amount: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }


}