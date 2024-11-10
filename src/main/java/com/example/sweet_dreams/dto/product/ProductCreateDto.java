package com.example.sweet_dreams.dto.product;

import com.example.sweet_dreams.model.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    @NotBlank(message = "Название обязательно")
    private String name;

    private String description;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    private Product.CakeSize size;

    @PositiveOrZero(message = "Вес должен быть положительным")
    private Double weight;

//    private Set<String> ingredients;

    private boolean custom;

    private MultipartFile mainImage;

    @PositiveOrZero(message = "Время приготовления должно быть положительным")
    private Integer preparationTimeHours;

    @PositiveOrZero(message = "Минимальное время заказа должно быть положительным")
    private Integer minimumOrderTimeHours;

    @NotEmpty(message = "Необходимо указать хотя бы одну цену для размера")
    private Map<Product.CakeSize, BigDecimal> sizePrices;

    @AssertTrue(message = "Все цены должны быть положительными")
    public boolean isSizePricesValid() {
        if (sizePrices == null || sizePrices.isEmpty()) {
            return false;
        }
        return sizePrices.values().stream()
                .allMatch(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0);
    }
}
