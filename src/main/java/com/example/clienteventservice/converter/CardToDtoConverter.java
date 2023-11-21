//package com.example.clienteventservice.converter;
//
//import org.springframework.stereotype.Component;
//
///**
// * Card to CardDto converter
// */
//@Component
//public class CardToDtoConverter implements Converter<Card, CardDto> {
//
//    @Override
//    public CardDto convert(Card card) {
//
//        return CardDto.builder()
//                .cardType(card.getCardType())
//                .number(card.getNumber())
//                .expiryDate(card.getExpiryDate())
//                .cvv(card.getCvv())
//                .build();
//    }
//
//}
