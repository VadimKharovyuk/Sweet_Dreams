package com.example.sweet_dreams.service;
import com.example.sweet_dreams.dto.discount.DiscountCreateDto;
import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.dto.discount.DiscountUpdateDto;
import com.example.sweet_dreams.dto.order.CartItem;
import com.example.sweet_dreams.exception.ResourceNotFoundException;
import com.example.sweet_dreams.maper.DiscountMapper;
import com.example.sweet_dreams.model.Discount;
import com.example.sweet_dreams.repository.DiscountRepository;
import com.example.sweet_dreams.service.serviceImpl.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponseDto> getAllDiscounts() {
        return discountMapper.toDtoList(discountRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponseDto> getActiveDiscounts() {
        return discountMapper.toDtoList(
                discountRepository.findActiveDiscounts(LocalDateTime.now())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponseDto findByCode(String code) {
        return discountRepository.findByCodeIgnoreCase(code)
                .map(discountMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Купон не найден"));
    }

    @Override
    public DiscountResponseDto createDiscount(DiscountCreateDto createDto) {
        if (createDto.getType().isCoupon() && createDto.getCode() != null) {
            if (discountRepository.existsByCode(createDto.getCode())) {
                throw new IllegalArgumentException("Купон с таким кодом уже существует");
            }
        }

        Discount discount = discountMapper.toEntity(createDto);
        Discount savedDiscount = discountRepository.save(discount);
        return discountMapper.toDto(savedDiscount);
    }

    @Override
    public DiscountResponseDto updateDiscount(Long id, DiscountUpdateDto updateDto) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Скидка не найдена"));

        discountMapper.updateEntityFromDto(discount, updateDto);
        Discount updatedDiscount = discountRepository.save(discount);
        return discountMapper.toDto(updatedDiscount);
    }

    @Override
    public void deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Скидка не найдена");
        }
        discountRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void incrementUsageCount(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Скидка не найдена"));

        discount.setCurrentUsageCount(discount.getCurrentUsageCount() + 1);
        discountRepository.save(discount);
    }

    @Override
    public BigDecimal calculateFinalPrice(List<CartItem> cart, String couponCode) {
        BigDecimal subtotal = calculateSubtotal(cart);

        // Если купона нет или он пустой, возвращаем обычную цену
        if (couponCode == null || couponCode.trim().isEmpty()) {
            log.info("No coupon provided, returning original price: {}", subtotal);
            return subtotal;
        }

        try {
            Discount discount = discountRepository.findByCodeIgnoreCase(couponCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Купон не найден"));

            if (!isValidDiscount(discount, subtotal)) {
                log.warn("Coupon {} is not valid for amount {}", couponCode, subtotal);
                return subtotal;
            }

            BigDecimal discountAmount = calculateDiscount(subtotal, discount);
            BigDecimal finalPrice = subtotal.subtract(discountAmount);

            log.info("Calculated price with discount: original={}, discount={}, final={}",
                    subtotal, discountAmount, finalPrice);

            return finalPrice;
        } catch (ResourceNotFoundException e) {
            log.warn("Coupon not found: {}, returning original price", couponCode);
            return subtotal;
        } catch (Exception e) {
            log.error("Error calculating final price with coupon: {}", couponCode, e);
            return subtotal;
        }
    }

    @Override
    public boolean isValidCoupon(String couponCode, BigDecimal subtotal) {
        // Если купона нет, считаем это валидным случаем
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return true;
        }

        try {
            Discount discount = discountRepository.findByCodeIgnoreCase(couponCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Купон не найден"));

            return isValidDiscount(discount, subtotal);
        } catch (Exception e) {
            log.error("Error validating coupon: {}", couponCode, e);
            return false;
        }
    }

    private boolean isValidDiscount(Discount discount, BigDecimal orderAmount) {
        LocalDateTime now = LocalDateTime.now();

        // Проверяем активность купона
        if (!discount.isActive()) {
            log.warn("Купон не активен");
            return false;
        }

        // Проверяем срок действия
        if (now.isBefore(discount.getValidFrom())) {
            log.warn("Срок действия купона еще не начался");
            return false;
        }
        if (now.isAfter(discount.getValidUntil())) {
            log.warn("Срок действия купона истек");
            return false;
        }

        // Проверяем количество использований
        if (discount.getMaxUsageCount() != null &&
                discount.getCurrentUsageCount() >= discount.getMaxUsageCount()) {
            log.warn("Превышен лимит использования купона");
            return false;
        }

        // Проверяем минимальную сумму заказа
        if (discount.getMinimumOrderAmount() != null &&
                orderAmount.compareTo(discount.getMinimumOrderAmount()) < 0) {
            log.warn("Сумма заказа {} меньше минимальной необходимой суммы {}",
                    orderAmount, discount.getMinimumOrderAmount());
            return false;
        }

        log.info("Купон {} валиден для заказа на сумму {}", discount.getCode(), orderAmount);
        return true;
    }

    private BigDecimal calculateSubtotal(List<CartItem> cart) {
        return cart.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, Discount discount) {
        if (discount.getType().isPercentage()) {
            return subtotal.multiply(discount.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            return discount.getValue().min(subtotal);
        }
    }

}