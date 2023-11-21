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
// * BankAccountDto to BankAccount converter
// */
//@Component
//public class DtoToBankAccountConverter implements Converter<BankAccountDto, BankAccount> {
//
//
//    private final ConversionService conversionService;
//
//    /*
//     * CAUTION: service must be lazy, because you cannot simply inject  a service that is not yet ready.
//     * We are already still building the converters in this step.
//     * */
//    @Autowired
//    public DtoToBankAccountConverter(@Lazy ConversionService conversionService) {
//        this.conversionService = conversionService;
//    }
//
//    @Override
//    public BankAccount convert(BankAccountDto bankAccount) {
//        return BankAccount.builder()
//                .iban(bankAccount.getIban())
//                .currentBalance(bankAccount.getBalance())
////                .card(conversionService.convert(bankAccount.getCard(), Card.class))
//                .build();
//    }
//
//}
