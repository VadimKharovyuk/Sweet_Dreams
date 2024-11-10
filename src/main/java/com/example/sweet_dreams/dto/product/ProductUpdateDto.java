package com.example.sweet_dreams.dto.product;

import com.example.sweet_dreams.model.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class ProductUpdateDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Product.CakeSize size;
    private Double weight;
    private Set<String> ingredients;
    private boolean custom;
    private String mainImage;
    private boolean available;
    private Integer preparationTimeHours;
    private Integer minimumOrderTimeHours;
    private Map<Product.CakeSize, BigDecimal> sizePrices;
}
