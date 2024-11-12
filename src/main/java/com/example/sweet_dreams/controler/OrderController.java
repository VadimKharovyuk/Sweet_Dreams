package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.order.CartItem;
import com.example.sweet_dreams.dto.order.OrderDTO;
import com.example.sweet_dreams.dto.order.OrderItemDTO;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.model.Order;
import com.example.sweet_dreams.service.OrderService;
import com.example.sweet_dreams.service.ProductService;
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


    // Показать страницу оформления заказа
    @GetMapping("/checkout")
    public String showCheckoutForm(HttpSession session, Model model) {
        // Получаем корзину из сессии
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);

        // Логируем для отладки
        log.debug("Retrieved cart from session: {}", cart);

        // Если корзина null, создаем пустую
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        // Проверка на пустую корзину
        if (cart.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        // Проверяем доступность всех товаров
        if (!areAllProductsAvailable(cart)) {
            return "redirect:/cart?error=unavailable";
        }

        // Если форма ещё не заполнялась, создаем новый DTO
        if (!model.containsAttribute("orderDTO")) {
            model.addAttribute("orderDTO", new OrderDTO());
        }

        BigDecimal total = calculateTotal(cart);
        model.addAttribute("cartItems", cart);
        model.addAttribute("total", total);

        // Логируем данные, передаваемые в представление
        log.debug("Sending to view - cartItems: {}, total: {}", cart, total);

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
            model.addAttribute("cartItems", cart);
            model.addAttribute("total", calculateTotal(cart));
            return "orders/checkout";
        }

        try {
            // Преобразуем CartItem в OrderItemDTO
            List<OrderItemDTO> orderItems = cart.stream()
                    .map(cartItem -> OrderItemDTO.builder()
                            .productId(cartItem.getProductId())
                            .productName(cartItem.getProductName())
                            .quantity(cartItem.getQuantity())
                            .price(cartItem.getPrice())
                            .build())
                    .collect(Collectors.toList());

            // Устанавливаем преобразованные items в DTO заказа
            orderDTO.setItems(orderItems);

            // Создаем заказ
            Order order = orderService.create(orderDTO);

            // Очищаем корзину после успешного оформления
            session.removeAttribute(CART_SESSION_KEY);

            return "redirect:/orders/confirmation/" + order.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Произошла ошибка при оформлении заказа. Пожалуйста, попробуйте снова.");
            redirectAttributes.addFlashAttribute("orderDTO", orderDTO);
            return "redirect:/orders/checkout";
        }
    }

    // Страница подтверждения заказа
    @GetMapping("/confirmation/{orderId}")
    public String showOrderConfirmation(@PathVariable Long orderId, Model model) {
        try {
            Order order = orderService.findById(orderId);
            if (order == null) {
                return "redirect:/orders?error=orderNotFound";
            }

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


    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    private BigDecimal calculateTotal(List<CartItem> cart) {
        return cart.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
