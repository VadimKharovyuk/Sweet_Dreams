package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
