package com.example.sweet_dreams.maper;

import com.example.sweet_dreams.dto.discount.DiscountCreateDto;
import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.dto.discount.DiscountUpdateDto;
import com.example.sweet_dreams.model.Discount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscountMapper {

    public Discount toEntity(DiscountCreateDto dto) {
        Discount discount = new Discount();
        discount.setName(dto.getName());
        discount.setDescription(dto.getDescription());
        if (dto.getType().isCoupon()) {
            discount.setCode(dto.getCode().toUpperCase());
        }
        discount.setType(dto.getType());
        discount.setValue(dto.getValue());
        discount.setValidFrom(dto.getValidFrom());
        discount.setValidUntil(dto.getValidUntil());
        discount.setMaxUsageCount(dto.getMaxUsageCount());
        discount.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        discount.setActive(true);
        return discount;
    }

    public void updateEntityFromDto(Discount discount, DiscountUpdateDto dto) {
        if (dto.getName() != null) {
            discount.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            discount.setDescription(dto.getDescription());
        }
        if (dto.getValue() != null) {
            discount.setValue(dto.getValue());
        }
        if (dto.getValidUntil() != null) {
            discount.setValidUntil(dto.getValidUntil());
        }
        if (dto.getMaxUsageCount() != null) {
            discount.setMaxUsageCount(dto.getMaxUsageCount());
        }
        if (dto.getMinimumOrderAmount() != null) {
            discount.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        }
        if (dto.getActive() != null) {
            discount.setActive(dto.getActive());
        }
    }

    public DiscountResponseDto toDto(Discount discount) {
        DiscountResponseDto dto = new DiscountResponseDto();
        BeanUtils.copyProperties(discount, dto);
        dto.calculateAdditionalFields();
        return dto;
    }

    public List<DiscountResponseDto> toDtoList(List<Discount> discounts) {
        return discounts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
