package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Базовый поиск по статусу
    List<Order> findByStatus(Order.OrderStatus status);

    // Поиск по критериям со статусом
    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:searchId IS NOT NULL AND o.id = :searchId OR " +
            "LOWER(o.customerName) LIKE CONCAT('%', :query, '%') OR " +
            "LOWER(o.customerEmail) LIKE CONCAT('%', :query, '%') OR " +
            "LOWER(o.customerPhone) LIKE CONCAT('%', :query, '%') OR " +
            "LOWER(o.deliveryAddress) LIKE CONCAT('%', :query, '%'))")
    Page<Order> findBySearchCriteriaAndStatus(
            @Param("query") String query,
            @Param("searchId") Long searchId,
            @Param("status") Order.OrderStatus status,
            Pageable pageable);

    // Поиск только по критериям без статуса
    @Query("SELECT o FROM Order o WHERE " +
            ":searchId IS NOT NULL AND o.id = :searchId OR " +
            "LOWER(o.customerName) LIKE CONCAT('%', :query, '%') OR " +
            "LOWER(o.customerEmail) LIKE CONCAT('%', :query, '%') OR " +
            "LOWER(o.customerPhone) LIKE CONCAT('%', :query, '%') OR " +
            "LOWER(o.deliveryAddress) LIKE CONCAT('%', :query, '%')")
    Page<Order> findBySearchCriteria(
            @Param("query") String query,
            @Param("searchId") Long searchId,
            Pageable pageable);

}
