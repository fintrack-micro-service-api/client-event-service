package com.example.clienteventservice.controller;

import com.example.clienteventservice.domain.model.Subscription;
import com.example.clienteventservice.domain.response.ApiResponse;
import com.example.clienteventservice.domain.type.NotificationType;
import com.example.clienteventservice.service.SubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("api/v1/clients")
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/get-notification/{userId}")
    public ResponseEntity<?> getNotificationTypeByUserId(@PathVariable String userId) {
        List<Subscription> subscriptionDtoList = subscriptionService.getNotificationTypeByUserId(userId);
        ApiResponse<List<Subscription>> response = ApiResponse.<List<Subscription>>builder()
                .message("success")
                .status(200)
                .payload(subscriptionDtoList)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/save-telegram-notification-by-userId/{userId}")
    public ResponseEntity<?> saveTelegramNotificationByUserId(@PathVariable UUID userId) {
        String telegramType = String.valueOf(NotificationType.TELEGRAM);
        Subscription subscription = subscriptionService.saveTelegramNotificationByUserId(userId, telegramType);
        ApiResponse<Subscription> response = ApiResponse.<Subscription>builder()
                .message("save success")
                .status(HttpStatus.OK.value())
                .payload(subscription)
                .build();
        return ResponseEntity.ok().body(response);
    }

}
