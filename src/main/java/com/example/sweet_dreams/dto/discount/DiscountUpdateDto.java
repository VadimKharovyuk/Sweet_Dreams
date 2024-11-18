package com.example.sweet_dreams.dto.discount;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountUpdateDto {
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @DecimalMin(value = "0.0", message = "Значение скидки не может быть отрицательным")
    @DecimalMax(value = "100.0", message = "Процентная скидка не может превышать 100%")
    private BigDecimal value;

    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime validUntil;

    @Min(value = 1, message = "Минимальное количество использований должно быть больше 0")
    private Integer maxUsageCount;

    @DecimalMin(value = "0.0", message = "Минимальная сумма заказа не может быть отрицательной")
    private BigDecimal minimumOrderAmount;

    private Boolean active;
}
