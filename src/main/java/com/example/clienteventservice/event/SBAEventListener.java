package com.example.clienteventservice.event;

import com.example.clienteventservice.domain.event.TransactionHistorySaveEvent;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.clienteventservice.exception.BankAccountManagerException;
import com.example.clienteventservice.repository.TransactionHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Component
public class SBAEventListener {

    private TransactionHistoryRepository transactionHistoryRepository;
    private static final Logger LOGGER = LogManager.getLogger(SBAEventListener.class);


    @Async
    @EventListener
    public void handleTransactionHistorySaveEvent(TransactionHistorySaveEvent transactionHistorySaveEvent) {
        try {
            TransactionHistory fromTransactionHistory = transactionHistorySaveEvent.getFromTransactionHistory();
            TransactionHistory toTransactionHistory = transactionHistorySaveEvent.getToTransactionHistory();

            TransactionHistory savedFromTransactionHistory = transactionHistoryRepository.save(fromTransactionHistory);
            LOGGER.info("fromTransactionHistory is written. {}", savedFromTransactionHistory);

            Optional.ofNullable(toTransactionHistory).ifPresent(transactionHistory -> {
                // set correlationIds from savedFromTransactionHistory
                transactionHistory.setCorrelationId(savedFromTransactionHistory.getId());
                savedFromTransactionHistory.setCorrelationId(savedFromTransactionHistory.getId());

                // save toTransactionHistory
                TransactionHistory savedToTransactionHistory = transactionHistoryRepository.save(transactionHistory);
                LOGGER.info("toTransactionHistory is written. {}", savedToTransactionHistory);

                // update correlation Id
                transactionHistoryRepository.save(savedFromTransactionHistory);

            });
        } catch (BankAccountManagerException e) {
            LOGGER.error("", e);
        }
        catch (Exception e) {
            LOGGER.error("", BankAccountManagerException.to(e, "Error while inserting TransactionHistory"));
        }
    }
}
