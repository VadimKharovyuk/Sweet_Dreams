package com.example.sweet_dreams.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Data
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // Код купона (если это купон)
    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    // Значение скидки (процент или фиксированная сумма)
    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validUntil;

    private Integer maxUsageCount;

    @Column(columnDefinition = "integer default 0")
    private Integer currentUsageCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    private BigDecimal minimumOrderAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Getter
    public enum DiscountType {
        FIXED_AMOUNT("Фиксированная сумма"),
        PERCENTAGE("Процентная скидка"),
        FIXED_AMOUNT_COUPON("Купон на фикс. сумму"),
        PERCENTAGE_COUPON("Купон на процент");

        private final String description;

        DiscountType(String description) {
            this.description = description;
        }

        public boolean isCoupon() {
            return this == FIXED_AMOUNT_COUPON || this == PERCENTAGE_COUPON;
        }

        public boolean isPercentage() {
            return this == PERCENTAGE || this == PERCENTAGE_COUPON;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
