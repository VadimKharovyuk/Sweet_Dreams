package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByEmail(String email);
    List<Subscriber> findAllByActiveTrue();

    Optional<Subscriber> findByEmail(String email);

}
