package com.example.sweet_dreams.dto.product;

import com.example.sweet_dreams.model.Product;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
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


    @PositiveOrZero(message = "Вес должен быть положительным")
    private Double weight;


    private boolean custom;

    private MultipartFile mainImage;

    @PositiveOrZero(message = "Время приготовления должно быть положительным")
    private Integer preparationTimeHours;

    @PositiveOrZero(message = "Минимальное время заказа должно быть положительным")
    private Integer minimumOrderTimeHours;

    @Enumerated(EnumType.STRING)
    private Product.CakeSize size;


}
