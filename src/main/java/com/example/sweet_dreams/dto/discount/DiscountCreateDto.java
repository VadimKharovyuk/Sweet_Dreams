package com.example.sweet_dreams.dto.discount;

import com.example.sweet_dreams.model.Discount;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountCreateDto {
    @NotBlank(message = "Название скидки обязательно")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    // Код купона требуется только для типов FIXED_AMOUNT_COUPON и PERCENTAGE_COUPON
    @Pattern(regexp = "^[A-Za-z0-9_-]{4,20}$",
            message = "Код купона должен содержать от 4 до 20 символов (буквы, цифры, _ и -)")
    private String code;

    @NotNull(message = "Тип скидки обязателен")
    private Discount.DiscountType type;

    @NotNull(message = "Значение скидки обязательно")
    @DecimalMin(value = "0.0", message = "Значение скидки не может быть отрицательным")
    private BigDecimal value;

    @NotNull(message = "Дата начала действия обязательна")
    private LocalDateTime validFrom;

    @NotNull(message = "Дата окончания действия обязательна")
    private LocalDateTime validUntil;

    private Integer maxUsageCount;

    @DecimalMin(value = "0.0", message = "Минимальная сумма заказа не может быть отрицательной")
    private BigDecimal minimumOrderAmount;

    // Кастомная валидация для проверки дат
    @AssertTrue(message = "Дата окончания должна быть позже даты начала")
    public boolean isValidDateRange() {
        if (validFrom == null || validUntil == null) {
            return true;
        }
        return validUntil.isAfter(validFrom);
    }

    // Кастомная валидация для значения скидки в зависимости от типа
    @AssertTrue(message = "Некорректное значение скидки для выбранного типа")
    public boolean isValidDiscountValue() {
        if (type == null || value == null) {
            return true;
        }

        if (type.isPercentage()) {
            return value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(new BigDecimal("100")) <= 0;
        } else {
            return value.compareTo(BigDecimal.ZERO) >= 0;
        }
    }

    // Кастомная валидация для кода купона
    @AssertTrue(message = "Код купона обязателен для купонов")
    public boolean isValidCouponCode() {
        if (type == null) {
            return true;
        }

        if (type.isCoupon()) {
            return code != null && !code.trim().isEmpty();
        }
        return true;
    }
}