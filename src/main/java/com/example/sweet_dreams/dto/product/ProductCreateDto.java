package com.example.sweet_dreams.dto.product;

import com.example.sweet_dreams.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    private Set<String> ingredients;

    private boolean custom;

    private MultipartFile mainImage;

    @PositiveOrZero(message = "Время приготовления должно быть положительным")
    private Integer preparationTimeHours;

    @PositiveOrZero(message = "Минимальное время заказа должно быть положительным")
    private Integer minimumOrderTimeHours;

    private Map<Product.CakeSize, BigDecimal> sizePrices;
}
