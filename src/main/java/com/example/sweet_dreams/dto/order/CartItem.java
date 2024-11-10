package com.example.sweet_dreams.dto.order;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItem {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
