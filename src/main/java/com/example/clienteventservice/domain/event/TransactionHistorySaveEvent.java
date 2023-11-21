package com.example.clienteventservice.domain.event;

import com.example.clienteventservice.domain.model.TransactionHistory;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Builder
@Getter
public class TransactionHistorySaveEvent extends ApplicationEvent {

    private transient Object eventSource;
    private TransactionHistory fromTransactionHistory;
    private TransactionHistory toTransactionHistory;

    public TransactionHistorySaveEvent(Object eventSource, TransactionHistory fromTransactionHistory, TransactionHistory toTransactionHistory) {
        super(eventSource);
        this.fromTransactionHistory = fromTransactionHistory;
        this.toTransactionHistory = toTransactionHistory;
    }
}
