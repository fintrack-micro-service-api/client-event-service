package com.example.clienteventservice.controller;

import com.example.clienteventservice.domain.dto.AmountDto;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.clienteventservice.service.DepositService;
import com.example.clienteventservice.service.TransactionService;
import com.example.clienteventservice.service.TransferService;
import com.example.clienteventservice.service.WithdrawService;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.clienteventservice.domain.response.ApiResponse;

import java.util.List;

@Api("Transfer services")
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private static final Logger LOG = LogManager.getLogger(TransactionController.class);
    private DepositService depositService;
    private TransferService transferService;
    private WithdrawService withdrawService;
    private TransactionService transactionService;

    @ApiOperation(value = "Withdraw from an account")
    @PostMapping(value = "/withdraw/{bankAccountId}")
    public ResponseEntity<?> withdraw(
            @ApiParam(value = "The ID of the bank account") @PathVariable(name = "bankAccountId") String bankAccountNumber,
            @ApiParam(value = "The amount of the withdraw transaction") @RequestBody @Valid AmountDto amountDto) {
        LOG.info("/{} called with amount: {}", bankAccountNumber, amountDto);

        ApiResponse<Void> withdrawResponse = withdrawService.withdraw(bankAccountNumber, amountDto.getAmount());

        return ResponseEntity.status(withdrawResponse.getStatus())
                .body(new ApiResponse<>(withdrawResponse.getMessage(), withdrawResponse.getStatus()));
    }

    @ApiOperation(value = "Transfer money from an account to other account")

    @PostMapping(value = "/transfer/{fromBankAccountId}/{toBankAccountId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> transfer(
            @ApiParam(value = "The ID of the from bank account") @PathVariable(name = "fromBankAccountId") String fromBankAccountNumber,
            @ApiParam(value = "The ID of the to bank account") @PathVariable(name = "toBankAccountId") String toBankAccountNumber,
            @ApiParam(value = "The amount of the withdraw transaction") @RequestBody @Valid AmountDto amountDto) {
        LOG.info("/{}/{} called with amount: {}", fromBankAccountNumber, toBankAccountNumber, amountDto);

        ApiResponse<Void> transferResponse = transferService.transfer(fromBankAccountNumber, toBankAccountNumber, amountDto.getAmount());

        return ResponseEntity.status(transferResponse.getStatus())
                .body(new ApiResponse<>(transferResponse.getMessage(), transferResponse.getStatus()));
    }

    @ApiOperation(value = "Deposit the money")
    @PostMapping(value = "/deposit/{accountNumber}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> deposit(
            @ApiParam(value = "The ID of the to bank account") @PathVariable(name = "accountNumber") String accountNumber,
            @ApiParam(value = "The amount of the withdraw transaction") @RequestBody @Valid AmountDto amountDto) {
        LOG.info("/{} called with amount: {}", accountNumber, amountDto);

        ApiResponse<Void> depositResponse = depositService.deposit(accountNumber, amountDto.getAmount());

        return ResponseEntity.status(depositResponse.getStatus())
                .body(new ApiResponse<>(depositResponse.getMessage(), depositResponse.getStatus()));
    }

    @ApiOperation(value = "Get transaction history")

    @GetMapping(value = "/history/{bankAccountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionHistory>>> getTransactionHistoryByAccountNumber(
            @ApiParam(value = "The bank account number") @PathVariable String bankAccountNumber) {
        ApiResponse<List<TransactionHistory>> response = transactionService.getTransactionHistoryByAccountNumber(bankAccountNumber);

        return ResponseEntity.status(response.getStatus()).body(response);
    }



}
