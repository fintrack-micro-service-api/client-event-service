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
 * Transfer process management service
 */
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class TransferService {

    private TransactionService transactionService;
    private BankAccountService bankAccountService;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public ApiResponse<Void> transfer(String fromBankAccountNumber, String toBankAccountNumber, BigDecimal amount) {
        try {
            ApiResponse<BankAccount> fromBankAccount = bankAccountService.getBankAccount(fromBankAccountNumber);
            ApiResponse<BankAccount> toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);

            if (fromBankAccount.getStatus() == HttpStatus.NOT_FOUND.value()) {
                return ApiResponse.<Void>builder()
                        .message("fromBankAccount not found")
                        .status(HttpStatus.NOT_FOUND.value())
                        .build();
            }

            if (toBankAccount.getStatus() == HttpStatus.NOT_FOUND.value()) {
                return ApiResponse.<Void>builder()
                        .message("toBankAccount not found")
                        .status(HttpStatus.NOT_FOUND.value())
                        .build();
            }

            transactionService.executeTransfer(fromBankAccount.getPayload(), toBankAccount.getPayload(), amount);

            TransactionHistory senderResponse = transactionService
                    .getTransactionHistoryBuilder(
                            TransactionType.SENDER,
                            StatementType.EXPENSE,
                            fromBankAccount.getPayload(),
                            amount
                    ).build();

            TransactionHistory receiverResponse = transactionService
                    .getTransactionHistoryBuilder(
                            TransactionType.RECEIVER,
                            StatementType.INCOME,
                            toBankAccount.getPayload(),
                            amount
                    ).build();

            senderResponse.setReceivedAccountNumber(toBankAccountNumber);
            receiverResponse.setReceivedAccountNumber(fromBankAccountNumber);

            sendTransactionNotification(senderResponse);
            sendTransactionNotification(receiverResponse);

            return ApiResponse.<Void>builder()
                    .message("Transfer successful")
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

    private void sendTransactionNotification(TransactionHistory history) {
        Message<TransactionHistoryDto> message = MessageBuilder
                .withPayload(history.toDto())
                .setHeader(KafkaHeaders.TOPIC, "notification-alert")
                .build();
        kafkaTemplate.send(message);

    }


}
