package com.example.clienteventservice.domain.response;


import com.example.clienteventservice.domain.model.TransactionHistory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T payload;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LoginResponse loginResponse;
    private Integer status;

    public ApiResponse(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponse(String message, T payload, Integer status) {
        this.message = message;
        this.payload = payload;
        this.status = status;
    }

    public ApiResponse(String message, Integer status, LoginResponse loginResponse) {
        this.message = message;
        this.loginResponse = loginResponse;
        this.status = status;
    }

    public ApiResponse(String getDataSuccessfully, List<TransactionHistory> transactionHistories, int value) {
    }
}
