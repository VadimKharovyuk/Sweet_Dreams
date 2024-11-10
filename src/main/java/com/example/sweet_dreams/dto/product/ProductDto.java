package com.example.sweet_dreams.dto.product;

import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.model.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private CategoryDto category;
    private Product.CakeSize size;
    private Double weight;
    private Set<String> ingredients;
    private boolean custom;
    private String mainImageBase64;
    private boolean available;
    private Integer preparationTimeHours;
    private Integer minimumOrderTimeHours;
    private Double averageRating;
    private Integer reviewsCount;
    private Map<Product.CakeSize, BigDecimal> sizePrices;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
