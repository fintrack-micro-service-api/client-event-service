package com.example.clienteventservice.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ClientConsumer {


    @KafkaListener(topics = "client-service")

    public void sendNotificationToWebPush(ConsumerRecord<String, String> commandsRecord) throws MessagingException, IOException {

    }


}




