//package com.example.clienteventservice.converter;
//
//import com.example.clienteventservice.domain.dto.CustomerDto;
//import com.example.clienteventservice.domain.model.Customer;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.stereotype.Component;
//
///**
// * Customer to CustomerDto converter
// */
//@Component
//public class CustomerToDtoConverter implements Converter<Customer, CustomerDto> {
//
//    @Override
//    public CustomerDto convert(Customer customer) {
//
//        return CustomerDto.builder()
//                .firstName(customer.getFirstName())
//                .lastName(customer.getLastName())
//                .username(customer.getUsername())
//                .email(customer.getEmail())
//                .phoneNumber(customer.getPhoneNumber())
//                .build();
//    }
//
//}
