package com.example.sweet_dreams.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPricing {
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal finalPrice;
}
