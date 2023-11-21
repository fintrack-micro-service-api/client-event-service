package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.dto.UserDtoClient;
import com.example.clienteventservice.domain.response.ApiResponse;
import com.example.clienteventservice.event.SBAEventListener;
import com.example.clienteventservice.exception.BankAccountManagerException;
import com.example.clienteventservice.exception.NotFoundException;
import com.example.clienteventservice.repository.BankAccountRepository;
import com.example.clienteventservice.domain.dto.BankAccountDto;
import com.example.clienteventservice.domain.model.BankAccount;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BankAccount management service
 */
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class BankAccountService {
    private static final Logger LOG = LogManager.getLogger(SBAEventListener.class);


    private static final String MESSAGE_FORMAT_NO_BANK_ACCOUNT = "No bankAccount by bankAccountId: %s";

    private BankAccountRepository bankAccountRepository;

    private final UserService userService;


    public ApiResponse<BankAccount> addBankAccount(UUID customerId, BankAccountDto bankAccountDto) {
        try {
            Preconditions.checkNotNull(bankAccountDto, "bankAccount can not be null");
            Preconditions.checkArgument(
                    bankAccountDto.getAccountNumber().matches("\\d{10}"),
                    "Bank AccountNumber must be 10 digits"
            );

            if (bankAccountRepository.existsByAccountNumber(bankAccountDto.getAccountNumber())) {
                throw new IllegalArgumentException("Bank Account Number already exists");
            }

            Preconditions.checkNotNull(bankAccountDto.getBalance(), "currentBalance can not be null");
            Preconditions.checkArgument(
                    bankAccountDto.getBalance().compareTo(BigDecimal.ZERO) > -1 && bankAccountDto.getBalance().compareTo(new BigDecimal("5")) >= 0,
                    "CurrentBalance must be non-negative and at least $5"
            );

            if (bankAccountRepository.existsByCustomerId(customerId)) {
                throw new IllegalArgumentException("Customer with ID already has a bank account");
            }

            ApiResponse<UserDtoClient> userDtoClient = userService.getById(customerId);

            if (userDtoClient.getPayload() == null) {
                throw new NotFoundException("User not found with ID: " + customerId);
            }

            System.out.println("userId in table: " + userDtoClient.getPayload().getId());

            BankAccount bankAccount = bankAccountDto.toEntity();
            bankAccount.setCustomerId(userDtoClient.getPayload().getId());

            BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
            LOG.info("A bank account saved for customer: {}", userDtoClient.getPayload().getId());

            return ApiResponse.<BankAccount>builder()
                    .message("Bank account created successfully")
                    .status(HttpStatus.CREATED.value())
                    .payload(savedBankAccount)
                    .build();
        } catch (NotFoundException | IllegalArgumentException e) {
            return ApiResponse.<BankAccount>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<BankAccount>builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }


    public ApiResponse<BankAccount> getBankAccount(String bankAccountNumber) {
        try {
            Preconditions.checkNotNull(bankAccountNumber, MESSAGE_FORMAT_NO_BANK_ACCOUNT, bankAccountNumber);

            Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByAccountNumber(bankAccountNumber);

            return getBankAccountApiResponse(bankAccountOptional);
        } catch (Exception e) {
            return ApiResponse.<BankAccount>builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    private ApiResponse<BankAccount> getBankAccountApiResponse(Optional<BankAccount> bankAccountOptional) {
        if (bankAccountOptional.isPresent()) {
            BankAccount bankAccount = bankAccountOptional.get();
            return ApiResponse.<BankAccount>builder()
                    .message("Bank account retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .payload(bankAccount)
                    .build();
        } else {
            return ApiResponse.<BankAccount>builder()
                    .message("Bank account not found")
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
        }
    }


    public ApiResponse<List<BankAccount>> getBankAccountList() {
        try {
            List<BankAccount> bankAccounts = bankAccountRepository.findAll();

            if (bankAccounts.isEmpty()) {
                return ApiResponse.<List<BankAccount>>builder()
                        .message("No bank accounts found")
                        .status(HttpStatus.NOT_FOUND.value())
                        .build();
            }

            return ApiResponse.<List<BankAccount>>builder()
                    .message("Bank accounts retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .payload(bankAccounts)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<BankAccount>>builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    public BankAccount decreaseCurrentBalance(BankAccount bankAccount, BigDecimal amount) {
        int effectedRows = bankAccountRepository.decreaseCurrentBalance(bankAccount.getId(), amount);
        if (effectedRows == 0) {
            throw BankAccountManagerException.to(
                    "The bank account is not effected of withdraw");
        }

        return bankAccountRepository.findById(bankAccount.getId())
                .orElseThrow(() -> BankAccountManagerException.to(MESSAGE_FORMAT_NO_BANK_ACCOUNT, bankAccount.getId()));
    }

    public BankAccount increaseCurrentBalance(BankAccount bankAccount, BigDecimal amount) {
        int effectedRows = bankAccountRepository.increaseCurrentBalance(bankAccount.getId(), amount);
        if (effectedRows == 0) {
            throw BankAccountManagerException.to(
                    "The bank account is not effected of transfer");
        }

        return bankAccountRepository.findById(bankAccount.getId())
                .orElseThrow(() -> BankAccountManagerException.to(MESSAGE_FORMAT_NO_BANK_ACCOUNT, bankAccount.getId()));
    }

    public ApiResponse<BankAccount> getBankAccountByUserId(UUID userId) {
        try {
            Preconditions.checkNotNull(userId, MESSAGE_FORMAT_NO_BANK_ACCOUNT, userId);

            Optional<BankAccount> bankAccountOptional = bankAccountRepository.findByCustomerId(userId);

            return getBankAccountApiResponse(bankAccountOptional);
        } catch (Exception e) {
            return ApiResponse.<BankAccount>builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }


}
