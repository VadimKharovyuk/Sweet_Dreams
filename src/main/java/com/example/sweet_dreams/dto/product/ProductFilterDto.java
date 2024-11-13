package com.example.sweet_dreams.dto.product;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductFilterDto {
    @DecimalMin(value = "0.0", message = "Минимальная цена не может быть отрицательной")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", message = "Максимальная цена не может быть отрицательной")
    private BigDecimal maxPrice;

    private Long categoryId;

    @DecimalMin(value = "0.0", message = "Минимальный рейтинг не может быть отрицательным")
    @DecimalMax(value = "5.0", message = "Максимальный рейтинг не может быть больше 5")
    private Double minRating;

    @Pattern(regexp = "^(price|rating|id)$", message = "Недопустимое поле для сортировки")
    private String sortBy;

    @Pattern(regexp = "^(asc|desc)$", message = "Направление сортировки должно быть 'asc' или 'desc'")
    private String sortDirection;
}
