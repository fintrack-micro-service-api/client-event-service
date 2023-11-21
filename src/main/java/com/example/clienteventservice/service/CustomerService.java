//package com.example.clienteventservice.service;
//
//import com.example.clienteventservice.domain.dto.CustomerDto;
//import com.example.clienteventservice.domain.model.Customer;
//import com.example.clienteventservice.event.SBAEventListener;
//import com.example.clienteventservice.exception.BankAccountManagerException;
//import com.example.clienteventservice.repository.CustomerRepository;
//import com.google.common.base.Preconditions;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * Customer management service
// */
//@Slf4j
//@Service
//public class CustomerService {
//
//    private static final String MESSAGE_FORMAT_NO_CUSTOMER = "No customer by customerId: %d";
//    private static final Logger LOG = LogManager.getLogger(CustomerService.class);
//
//
//    private final CustomerRepository customerRepository;
//
//    public CustomerService(CustomerRepository customerRepository) {
//        this.customerRepository = customerRepository;
//    }
//
//    public List<Customer> getCustomerList() {
//        return customerRepository.findAll();
//    }
//
//    public Customer getCustomer(Long customerId) {
//        Preconditions.checkNotNull(customerId, MESSAGE_FORMAT_NO_CUSTOMER, customerId);
//
//        return customerRepository.findById(customerId)
//                .orElseThrow(() -> BankAccountManagerException.to(MESSAGE_FORMAT_NO_CUSTOMER, customerId));
//    }
//
//    public Customer saveCustomer(Customer customer) {
//        Preconditions.checkNotNull(customer, "customer can not be null");
//
//        Customer savedCustomer = customerRepository.save(customer);
//        LOG.info("Customer saved by id: {}", savedCustomer.getId());
//
//        return savedCustomer;
//    }
//}
