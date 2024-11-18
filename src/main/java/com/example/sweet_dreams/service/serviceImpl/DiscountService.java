package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.discount.DiscountCreateDto;
import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.dto.discount.DiscountUpdateDto;
import com.example.sweet_dreams.dto.order.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface DiscountService {
    List<DiscountResponseDto> getAllDiscounts();

    List<DiscountResponseDto> getActiveDiscounts();

    DiscountResponseDto findByCode(String code);

    DiscountResponseDto createDiscount(DiscountCreateDto createDto);

    DiscountResponseDto updateDiscount(Long id, DiscountUpdateDto updateDto);


    void deleteDiscount(Long id);


    void incrementUsageCount(Long discountId);


    BigDecimal calculateFinalPrice(List<CartItem> cart, String couponCode);

    boolean isValidCoupon(String couponCode, BigDecimal subtotal);

}