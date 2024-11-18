package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.dto.order.CartItem;
import com.example.sweet_dreams.dto.order.OrderDTO;
import com.example.sweet_dreams.dto.order.OrderDetailsDTO;
import com.example.sweet_dreams.dto.order.OrderItemDTO;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.model.Order;
import com.example.sweet_dreams.service.serviceImpl.DiscountService;
import com.example.sweet_dreams.service.serviceImpl.OrderService;
import com.example.sweet_dreams.service.serviceImpl.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ProductService productService;
    private static final String CART_SESSION_KEY = "cart";
    private final OrderService orderService;
    private final DiscountService discountService;


    // Показать страницу оформления заказа

    @GetMapping("/checkout")
    public String showCheckoutForm(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);

        if (cart.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        if (!model.containsAttribute("orderDTO")) {
            model.addAttribute("orderDTO", new OrderDTO());
        }

        BigDecimal subtotal = calculateTotal(cart);
        model.addAttribute("cartItems", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", subtotal);

        return "orders/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute OrderDTO orderDTO,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        List<CartItem> cart = getCart(session);

        if (cart.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        if (bindingResult.hasErrors()) {
            return showCheckoutForm(session, model);
        }

        try {
            // Подготавливаем items для заказа
            List<OrderItemDTO> orderItems = cart.stream()
                    .map(cartItem -> OrderItemDTO.builder()
                            .productId(cartItem.getProductId())
                            .productName(cartItem.getProductName())
                            .quantity(cartItem.getQuantity())
                            .price(cartItem.getPrice())
                            .build())
                    .collect(Collectors.toList());

            orderDTO.setItems(orderItems);

            // Рассчитываем финальную цену
            BigDecimal finalPrice;
            String couponCode = orderDTO.getCouponCode();

            if (couponCode != null && !couponCode.trim().isEmpty()) {
                try {
                    // Проверяем валидность купона
                    if (discountService.isValidCoupon(couponCode, calculateTotal(cart))) {
                        finalPrice = discountService.calculateFinalPrice(cart, couponCode);
                        log.info("Applied coupon: {}, final price: {}", couponCode, finalPrice);
                    } else {
                        // Если купон невалиден, используем обычную цену
                        finalPrice = calculateTotal(cart);
                        log.warn("Invalid coupon: {}, using original price: {}", couponCode, finalPrice);
                        // Очищаем невалидный купон
                        orderDTO.setCouponCode(null);
                    }
                } catch (Exception e) {
                    // В случае ошибки с купоном используем обычную цену
                    finalPrice = calculateTotal(cart);
                    log.error("Error processing coupon: {}", couponCode, e);
                    orderDTO.setCouponCode(null);
                }
            } else {
                // Если купона нет, используем обычную цену
                finalPrice = calculateTotal(cart);
                log.info("No coupon provided, using original price: {}", finalPrice);
            }

            // Создаем заказ
            Order order = orderService.create(orderDTO);

            // Увеличиваем счетчик использований купона только если он был успешно применен
            if (order.getCouponCode() != null) {
                try {
                    DiscountResponseDto discount = discountService.findByCode(order.getCouponCode());
                    discountService.incrementUsageCount(discount.getId());
                    log.info("Incremented usage count for coupon: {}", order.getCouponCode());
                } catch (Exception e) {
                    log.error("Error incrementing coupon usage count", e);
                    // Не прерываем процесс создания заказа из-за ошибки с купоном
                }
            }

            // Очищаем корзину
            session.removeAttribute(CART_SESSION_KEY);

            return "redirect:/orders/confirmation/" + order.getId();
        } catch (Exception e) {
            log.error("Error processing checkout", e);
            redirectAttributes.addFlashAttribute("error",
                    "Произошла ошибка при оформлении заказа. Пожалуйста, попробуйте снова.");
            redirectAttributes.addFlashAttribute("orderDTO", orderDTO);
            return "redirect:/orders/checkout";
        }
    }

    // Вспомогательные методы
    private List<CartItem> getCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        return cart != null ? cart : new ArrayList<>();
    }

    private BigDecimal calculateTotal(List<CartItem> cart) {
        return cart.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
@GetMapping("/confirmation/{orderId}")
public String showOrderConfirmation(@PathVariable Long orderId, Model model) {
    try {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/orders?error=orderNotFound";
        }

        OrderDetailsDTO orderDetails = OrderDetailsDTO.builder()
                .order(order)
                .subtotal(order.getSubtotalAmount())
                .discount(order.getDiscount())
                .total(order.getTotalAmount())
                .appliedCouponCode(order.getCouponCode())
                .build();

        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("order", order);

        return "orders/confirmation";
    } catch (Exception e) {
        log.error("Error showing order confirmation for orderId: " + orderId, e);
        return "redirect:/orders?error=orderError";
    }
}

    // Метод для обработки ошибок
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        log.error("Error in OrderController", e);
        model.addAttribute("error", "Произошла ошибка при обработке заказа");
        return "error/order-error";
    }


    private boolean areAllProductsAvailable(List<CartItem> cart) {
        return cart.stream()
                .allMatch(item -> orderService.isProductAvailable(item.getProductId()));
    }




    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            Model model) {
        // Получаем ProductDto
        ProductDto product = productService.findById(productId);

        // Проверяем доступность товара
        if (!product.isAvailable()) {
            return "redirect:/products/" + productId + "?error=notAvailable";
        }

        List<CartItem> cart = getCart(session);

        boolean itemExists = false;
        for (CartItem item : cart) {
            if (Objects.equals(item.getProductId(), productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setPrice(product.getPrice());
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(quantity);
            cart.add(cartItem);
        }

        session.setAttribute(CART_SESSION_KEY, cart);
        updateCartCountInModel(session, model);  // Добавляем обновление счетчика

        return "redirect:/orders/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        BigDecimal total = calculateTotal(cart);

        model.addAttribute("cartItems", cart);
        model.addAttribute("total", total);
        updateCartCountInModel(session, model);  // Добавляем обновление счетчика

        return "cart/view";
    }

    // Метод для обновления количества
    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam Long productId,
                                 @RequestParam Integer quantity,
                                 HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);

        if (cart != null) {
            for (CartItem item : cart) {
                if (Objects.equals(item.getProductId(), productId)) {
                    item.setQuantity(quantity);

                    // Обновляем цену на случай изменений
                    ProductDto product = productService.findById(productId);
                    item.setPrice(product.getPrice());
                    break;
                }
            }
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        return "redirect:/orders/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 HttpSession session,
                                 Model model) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> Objects.equals(item.getProductId(), productId));
        session.setAttribute(CART_SESSION_KEY, cart);
        updateCartCountInModel(session, model);  // Добавляем обновление счетчика

        return "redirect:/orders/cart";
    }

    // Вспомогательный метод для обновления счетчика корзины
    private void updateCartCountInModel(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        int cartCount = cart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        model.addAttribute("cartCount", cartCount);
    }


}
