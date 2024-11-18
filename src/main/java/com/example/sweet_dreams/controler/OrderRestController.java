package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.dto.order.CartItem;
import com.example.sweet_dreams.exception.ResourceNotFoundException;
import com.example.sweet_dreams.service.serviceImpl.DiscountService;
import com.example.sweet_dreams.service.serviceImpl.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderRestController {
    private final DiscountService discountService;

    @PostMapping("/apply-coupon")
    public ResponseEntity<?> applyCoupon(@RequestParam String code, HttpSession session) {
        try {
            // Получаем корзину из сессии
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null || cart.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Корзина пуста"));
            }

            // Рассчитываем общую сумму
            BigDecimal subtotal = calculateTotal(cart);

            // Проверяем существование и валидность купона
            DiscountResponseDto discount = discountService.findByCode(code);

            // Проверяем валидность купона до расчета скидки
            if (!isValidDiscount(discount, subtotal)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Купон недействителен"));
            }

            try {
                // Рассчитываем цену с учетом скидки
                BigDecimal finalPrice = discountService.calculateFinalPrice(cart, code);
                BigDecimal discountAmount = subtotal.subtract(finalPrice);

                // Сохраняем код купона в сессии
                session.setAttribute("appliedCouponCode", code);

                return ResponseEntity.ok(Map.of(
                        "subtotal", subtotal,
                        "discount", discountAmount,
                        "total", finalPrice,
                        "message", "Купон успешно применен"
                ));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", e.getMessage()));
            }

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Купон не найден"));
        } catch (Exception e) {
            log.error("Error applying coupon", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Произошла ошибка при применении купона"));
        }
    }

    private boolean isValidDiscount(DiscountResponseDto discount, BigDecimal orderAmount) {
        LocalDateTime now = LocalDateTime.now();

        // Проверяем активность купона
        if (!discount.isActive()) {
            return false;
        }

        // Проверяем срок действия
        if (now.isBefore(discount.getValidFrom()) || now.isAfter(discount.getValidUntil())) {
            return false;
        }

        // Проверяем количество использований
        if (discount.getMaxUsageCount() != null &&
                discount.getCurrentUsageCount() >= discount.getMaxUsageCount()) {
            return false;
        }

        // Проверяем минимальную сумму заказа
        if (discount.getMinimumOrderAmount() != null &&
                orderAmount.compareTo(discount.getMinimumOrderAmount()) < 0) {
            return false;
        }

        return true;
    }

    @DeleteMapping("/remove-coupon")
    public ResponseEntity<?> removeCoupon(HttpSession session) {
        try {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null || cart.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Корзина пуста"));
            }

            session.removeAttribute("appliedCouponCode");
            BigDecimal subtotal = calculateTotal(cart);

            return ResponseEntity.ok(Map.of(
                    "subtotal", subtotal,
                    "total", subtotal,
                    "message", "Купон успешно удален"
            ));
        } catch (Exception e) {
            log.error("Error removing coupon", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Произошла ошибка при удалении купона"));
        }
    }

    private BigDecimal calculateTotal(List<CartItem> cart) {
        return cart.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}