package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    List<Subscription> getNotificationTypeByUserId(String userId);

    Subscription saveTelegramNotificationByUserId(UUID userId, String notificationType);
}
