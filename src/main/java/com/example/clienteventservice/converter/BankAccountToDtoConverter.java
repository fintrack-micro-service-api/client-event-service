//package com.example.clienteventservice.converter;
//
//import com.example.clienteventservice.domain.dto.BankAccountDto;
//import com.example.clienteventservice.domain.model.BankAccount;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.core.convert.ConversionService;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.stereotype.Component;
//
///**
// * BankAccount to BankAccountDto converter
// */
//@Component
//public class BankAccountToDtoConverter implements Converter<BankAccount, BankAccountDto> {
//
//    /*
//     * CAUTION: service must be lazy, because you cannot simply inject  a service that is not yet ready.
//     * We are already still building the converters in this step.
//     * */
//    @Autowired
//    public BankAccountToDtoConverter(@Lazy ConversionService conversionService) {
//    }
//
//    @Override
//    public BankAccountDto convert(BankAccount bankAccount) {
//        return BankAccountDto.builder()
//                .iban(bankAccount.getIban())
//                .balance(bankAccount.getCurrentBalance())
////                .card(conversionService.convert(bankAccount.getCard(), CardDto.class))
//                .build();
//    }
//
//}
