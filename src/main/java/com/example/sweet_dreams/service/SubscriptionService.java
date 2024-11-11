package com.example.sweet_dreams.service;

import com.example.sweet_dreams.exception.AlreadySubscribedException;
import com.example.sweet_dreams.model.Subscriber;
import com.example.sweet_dreams.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class SubscriptionService {
    private final SubscriberRepository subscriberRepository;
    private final EmailService emailService;

    public void subscribe(String email) {
        if (subscriberRepository.existsByEmail(email)) {
            log.warn("Attempt to subscribe existing email: {}", email);
            throw new AlreadySubscribedException("Цей email вже підписаний на розсилку");
        }

        try {
            Subscriber subscriber = new Subscriber();
            subscriber.setEmail(email);
            subscriber.setSubscribedAt(LocalDateTime.now());
            subscriber.setActive(true);
            subscriberRepository.save(subscriber);

            emailService.sendSubscriptionConfirmation(email);
            log.info("New subscriber added: {}", email);
        } catch (Exception e) {
            log.error("Error during subscription process for email: {}", email, e);
            throw new RuntimeException("Помилка при оформленні підписки", e);
        }
    }

    public void unsubscribe(String email) {
        try {
            subscriberRepository.findByEmail(email)
                    .ifPresent(subscriber -> {
                        subscriber.setActive(false);
                        subscriberRepository.save(subscriber);
                        log.info("Subscriber unsubscribed: {}", email);
                    });
        } catch (Exception e) {
            log.error("Error during unsubscribe process for email: {}", email, e);
            throw new RuntimeException("Помилка при відписці", e);
        }
    }

    public List<Subscriber> getAllActiveSubscribers() {
        return subscriberRepository.findAllByActiveTrue();
    }
}
