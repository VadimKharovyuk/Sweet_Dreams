package com.example.sweet_dreams.dto.category;

import com.example.sweet_dreams.dto.product.ProductListDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CategoryWithProductsDto {
    private Long id;
    private String name;
    private String description;
    private List<ProductListDto> products;
    private BigDecimal minPrice;
}
