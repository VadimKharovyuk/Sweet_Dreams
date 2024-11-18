package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.dto.discount.OrderPricing;
import com.example.sweet_dreams.dto.order.CartItem;
import com.example.sweet_dreams.service.serviceImpl.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDiscountService {
    private final DiscountService discountService;

    public OrderPricing calculateOrderPricing(List<CartItem> items, String couponCode) {
        BigDecimal subtotal = calculateSubtotal(items);

        if (couponCode == null || couponCode.isEmpty()) {
            return new OrderPricing(subtotal, null, subtotal);
        }

        try {
            DiscountResponseDto discount = discountService.findByCode(couponCode);
            if (discountService.isValidCoupon(couponCode, subtotal)) {
                BigDecimal finalPrice = discountService.calculateFinalPrice(items, couponCode);
                BigDecimal discountAmount = subtotal.subtract(finalPrice);

                return new OrderPricing(subtotal, discountAmount, finalPrice);
            }
        } catch (Exception e) {
            log.error("Error applying discount", e);
        }

        return new OrderPricing(subtotal, null, subtotal);
    }

    private BigDecimal calculateSubtotal(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
