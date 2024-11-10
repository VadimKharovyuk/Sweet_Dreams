package com.example.sweet_dreams.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductListDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String mainImageBase64;
    private boolean available;
    private Double averageRating;
}
