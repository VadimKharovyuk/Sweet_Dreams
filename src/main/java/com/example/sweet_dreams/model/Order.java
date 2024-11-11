package com.example.sweet_dreams.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Информация о покупателе
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum OrderStatus {
        NEW("Новый заказ"),
        CONFIRMED("Подтвержден"),
        PREPARING("Готовится"),
        READY("Готов к выдаче"),
        DELIVERING("В процессе доставки"),
        COMPLETED("Выполнен"),
        CANCELLED("Отменен");

        private final String description;

        // Конструктор
        OrderStatus(String description) {
            this.description = description;
        }

        // Геттер для получения описания
        public String getDescription() {
            return description;
        }

    }
}
