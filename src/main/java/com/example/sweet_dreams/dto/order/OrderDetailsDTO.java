package com.example.sweet_dreams.dto.order;

import com.example.sweet_dreams.model.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderDetailsDTO {
    private Order order;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String appliedCouponCode;
}
