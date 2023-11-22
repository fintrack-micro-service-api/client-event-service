package com.example.clienteventservice.controller;

import com.example.clienteventservice.domain.dto.BalanceDto;
import com.example.clienteventservice.domain.dto.BankAccountDto;
import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.service.BankAccountService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import com.example.clienteventservice.domain.response.ApiResponse;
import java.util.stream.Collectors;

/**
 * This class is created to manage bank account process
 */
@Api("Bank account management services")
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping(BankAccountController.SERVICE_PATH)
public class BankAccountController {

    public static final String SERVICE_PATH = "api/v1/bank/";
    public static final String METHOD_GET_BALANCE = "/balance";
    private static final String METHOD_GET_BALANCE_WITH_PARAM = "/balance/{bankAccountNumber}";
    public static final String METHOD_GET_BALANCE_ALL = "/balance/all";

    private BankAccountService bankAccountService;
    private ConversionService conversionService;
    private static final Logger LOG = LogManager.getLogger(BankAccountController.class);


    @ApiOperation(value = "Create a new bank account with a credit card or debit card by given customerId")
    @PutMapping(value = "{customerId}")
    public ResponseEntity<?> saveAccount(
            @ApiParam(value = "The ID of the customer") @PathVariable(name = "customerId") UUID customerId,
            @ApiParam(value = "Bank account details") @RequestBody BankAccountDto bankAccountDto) {
        LOG.info("/{}/{} called with bankAccountDto: {}", SERVICE_PATH, customerId, bankAccountDto);

        com.example.clienteventservice.domain.response.ApiResponse<BankAccount> response =
                bankAccountService.addBankAccount(customerId, bankAccountDto);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ApiOperation(value = "Retrieves the current balances of all bank accounts", response = BalanceDto.class, responseContainer = "List")
    @GetMapping(value = METHOD_GET_BALANCE_ALL)
    public ResponseEntity<?> getBankAccountList() {
        ApiResponse<List<BankAccount>> response = bankAccountService.getBankAccountList();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ApiOperation(value = "Retrieves the current balance of a bank account", response = BalanceDto.class, responseContainer = "List")
    @GetMapping(value = METHOD_GET_BALANCE_WITH_PARAM)
    public ResponseEntity<?> getBalance(
            @ApiParam(value = "The bank account number") @PathVariable String bankAccountNumber) {
        ApiResponse<BankAccount> bankAccountResponse = bankAccountService.getBankAccount(bankAccountNumber);

        if (bankAccountResponse.getStatus() == HttpStatus.OK.value()) {
            BankAccount bankAccount = bankAccountResponse.getPayload();
            BalanceDto balanceDto = conversionService.convert(bankAccount, BalanceDto.class);
            return ResponseEntity.ok().body(ApiResponse.<BalanceDto>builder()
                    .message("Balance retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .payload(balanceDto)
                    .build());
        } else {
            return ResponseEntity.status(bankAccountResponse.getStatus()).body(ApiResponse.<BalanceDto>builder()
                    .message(bankAccountResponse.getMessage())
                    .status(bankAccountResponse.getStatus())
                    .build());
        }
    }

    @ApiOperation(value = "Retrieves bank info with user id", response = BalanceDto.class, responseContainer = "List")
    @GetMapping("/bankInfo/{userId}")
    public ResponseEntity<?> getAccountInfoByUserId(
            @ApiParam(value = "The user ID") @PathVariable UUID userId) {
        ApiResponse<BankAccount> bankAccountResponse = bankAccountService.getBankAccountByUserId(userId);

        if (bankAccountResponse.getStatus() == HttpStatus.OK.value()) {
            BankAccount bankAccount = bankAccountResponse.getPayload();
            BalanceDto balanceDto = conversionService.convert(bankAccount, BalanceDto.class);
            return ResponseEntity.ok().body(ApiResponse.<BalanceDto>builder()
                    .message("Account info retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .payload(balanceDto)
                    .build());
        } else {
            return ResponseEntity.status(bankAccountResponse.getStatus()).body(ApiResponse.<BalanceDto>builder()
                    .message(bankAccountResponse.getMessage())
                    .status(bankAccountResponse.getStatus())
                    .build());
        }
    }

    @ApiOperation(value = "Retrieves customer info with bank account number", response = BankAccount.class)
    @GetMapping("/customerInfo/{bankAccountNo}")
    public ResponseEntity<?> getAccountInfoByUserId(
            @ApiParam(value = "The bank account number") @PathVariable String bankAccountNo) {
        ApiResponse<BankAccount> bankAccountResponse = bankAccountService.getBankAccount(bankAccountNo);

        if (bankAccountResponse.getStatus() == HttpStatus.OK.value()) {
            BankAccount bankAccount = bankAccountResponse.getPayload();
            return ResponseEntity.ok(ApiResponse.<BankAccount>builder()
                    .message("Account info retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .payload(bankAccount)
                    .build());
        } else {
            return ResponseEntity.status(bankAccountResponse.getStatus()).body(ApiResponse.<BankAccount>builder()
                    .message(bankAccountResponse.getMessage())
                    .status(bankAccountResponse.getStatus())
                    .build());
        }
    }



}
