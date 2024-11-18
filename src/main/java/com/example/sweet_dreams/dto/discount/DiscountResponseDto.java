package com.example.sweet_dreams.dto.discount;

import com.example.sweet_dreams.model.Discount;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountResponseDto {
    private Long id;
    private String name;
    private String description;
    private String code;
    private Discount.DiscountType type;
    private BigDecimal value;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer maxUsageCount;
    private Integer currentUsageCount;
    private boolean active;
    private BigDecimal minimumOrderAmount;
    private LocalDateTime createdAt;

    // Дополнительные поля для отображения
    private boolean isExpired;
    private boolean isValid;
    private String formattedDiscount;
    private int remainingUses;

    public void calculateAdditionalFields() {
        LocalDateTime now = LocalDateTime.now();
        this.isExpired = now.isAfter(validUntil);
        this.isValid = active && !isExpired &&
                (maxUsageCount == null || currentUsageCount < maxUsageCount);

        this.formattedDiscount = type.isPercentage() ?
                value + "%" :
                value + " грн";

        this.remainingUses = maxUsageCount != null ?
                maxUsageCount - currentUsageCount :
                Integer.MAX_VALUE;
    }
}