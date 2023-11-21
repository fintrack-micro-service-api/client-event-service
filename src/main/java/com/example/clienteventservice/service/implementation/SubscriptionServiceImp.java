package com.example.clienteventservice.service.implementation;

import com.example.clienteventservice.repository.SubscriptionRepository;
import com.example.clienteventservice.domain.model.Subscription;
import com.example.clienteventservice.service.SubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SubscriptionServiceImp implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public List<Subscription> getNotificationTypeByUserId(String userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);

//        List<SubscriptionDto> subscriptionDtos = new ArrayList<>();
//
//        for (Subscription subscription : subscriptions) {
//            SubscriptionDto subscriptionDto = new SubscriptionDto();
//
//            subscriptionDto.setId(subscriptionDto.getId());
//            subscriptionDto.setUserId(subscription.getUserId());
//            subscriptionDto.setNotificationType(subscription.getNotificationType());
//
//            subscriptionDtos.add(subscriptionDto);
//        }

        return subscriptions;
    }


    @Override
    public Subscription saveTelegramNotificationByUserId(UUID userId, String notificationType) {
        Subscription subscription = new Subscription();
        subscription.setUserId(String.valueOf(userId));
        subscription.setNotificationType(notificationType);
        return subscriptionRepository.save(subscription);
    }
}
