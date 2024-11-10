package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.order.CartItem;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private static final String CART_SESSION_KEY = "cart";

    @GetMapping("/count")
    public Map<String, Integer> getCartCount(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        int count = 0;
        if (cart != null) {
            count = cart.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
        }
        return Map.of("count", count);
    }
}
