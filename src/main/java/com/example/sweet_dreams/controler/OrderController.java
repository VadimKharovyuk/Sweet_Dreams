package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.order.CartItem;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ProductService productService;
    private static final String CART_SESSION_KEY = "cart";

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

    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam Long productId,
                                 @RequestParam Integer quantity,
                                 HttpSession session,
                                 Model model) {
        ProductDto product = productService.findById(productId);
        if (!product.isAvailable()) {
            return "redirect:/cart?error=productNotAvailable";
        }

        List<CartItem> cart = getCart(session);

        for (CartItem item : cart) {
            if (Objects.equals(item.getProductId(), productId)) {
                item.setQuantity(quantity);
                item.setPrice(product.getPrice());
                break;
            }
        }

        session.setAttribute(CART_SESSION_KEY, cart);
        updateCartCountInModel(session, model);  // Добавляем обновление счетчика

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 HttpSession session,
                                 Model model) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> Objects.equals(item.getProductId(), productId));
        session.setAttribute(CART_SESSION_KEY, cart);
        updateCartCountInModel(session, model);  // Добавляем обновление счетчика

        return "redirect:/cart";
    }

    // Вспомогательный метод для обновления счетчика корзины
    private void updateCartCountInModel(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        int cartCount = cart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        model.addAttribute("cartCount", cartCount);
    }

    // Остальные методы без изменений
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



//package com.example.sweet_dreams.controler;
//
//import com.example.sweet_dreams.dto.order.CartItem;
//import com.example.sweet_dreams.dto.product.ProductDto;
// import com.example.sweet_dreams.model.Order;
//import com.example.sweet_dreams.service.OrderService;
//import com.example.sweet_dreams.service.ProductService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Controller
//@RequestMapping("/orders")
//@RequiredArgsConstructor
//public class OrderController {
//
//    private final ProductService productService;
//    private static final String CART_SESSION_KEY = "cart";
//
//    @GetMapping("/cart")
//    public String viewCart(HttpSession session, Model model) {
//        List<CartItem> cart = getCart(session);
//        BigDecimal total = calculateTotal(cart);
//
//
//        model.addAttribute("cartItems", cart);
//        model.addAttribute("total", total);
//
//
//        return "cart/view";
//    }
//
//    @PostMapping("/cart/add")
//    public String addToCart(@RequestParam Long productId,
//                            @RequestParam(defaultValue = "1") Integer quantity,
//                            HttpSession session) {
//        // Получаем ProductDto
//        ProductDto product = productService.findById(productId);
//
//        // Проверяем доступность товара
//        if (!product.isAvailable()) {
//            // Можно добавить сообщение об ошибке через RedirectAttributes
//            return "redirect:/products/" + productId + "?error=notAvailable";
//        }
//
//        List<CartItem> cart = getCart(session);
//
//        boolean itemExists = false;
//        for (CartItem item : cart) {
//            if (Objects.equals(item.getProductId(), productId)) {
//                item.setQuantity(item.getQuantity() + quantity);
//                // Обновляем цену на случай, если она изменилась
//                item.setPrice(product.getPrice());
//                itemExists = true;
//                break;
//            }
//        }
//
//        if (!itemExists) {
//            CartItem cartItem = new CartItem();
//            cartItem.setProductId(product.getId());
//            cartItem.setProductName(product.getName());
//            cartItem.setPrice(product.getPrice());
//            cartItem.setQuantity(quantity);
//            cart.add(cartItem);
//        }
//
//        session.setAttribute(CART_SESSION_KEY, cart);
//
//        return "redirect:/orders/cart";
//    }
//
//
//
//    @PostMapping("/cart/update")
//    public String updateCartItem(@RequestParam Long productId,
//                                 @RequestParam Integer quantity,
//                                 HttpSession session) {
//        // Проверяем актуальность товара
//        ProductDto product = productService.findById(productId);
//        if (!product.isAvailable()) {
//            return "redirect:/cart?error=productNotAvailable";
//        }
//
//        List<CartItem> cart = getCart(session);
//
//        for (CartItem item : cart) {
//            if (Objects.equals(item.getProductId(), productId)) {
//                item.setQuantity(quantity);
//                item.setPrice(product.getPrice()); // Обновляем цену
//                break;
//            }
//        }
//
//        session.setAttribute(CART_SESSION_KEY, cart);
//        return "redirect:/cart";
//    }
//
//    @PostMapping("/cart/remove")
//    public String removeFromCart(@RequestParam Long productId,
//                                 HttpSession session) {
//        List<CartItem> cart = getCart(session);
//        cart.removeIf(item -> Objects.equals(item.getProductId(), productId));
//        session.setAttribute(CART_SESSION_KEY, cart);
//
//        return "redirect:/cart";
//    }
//
//    @GetMapping("/checkout")
//    public String showCheckoutForm(HttpSession session, Model model) {
//        List<CartItem> cart = getCart(session);
//
//        if (cart.isEmpty()) {
//            return "redirect:/cart?error=empty";
//        }
//
//        // Проверяем актуальность всех товаров перед оформлением
//        boolean allProductsAvailable = cart.stream()
//                .allMatch(item -> {
//                    ProductDto product = productService.findById(item.getProductId());
//                    return product.isAvailable();
//                });
//
//        if (!allProductsAvailable) {
//            return "redirect:/cart?error=someProductsNotAvailable";
//        }
//
//        model.addAttribute("cartItems", cart);
//        model.addAttribute("total", calculateTotal(cart));
//
//        return "orders/checkout";
//    }
//
//    // Вспомогательные методы
//    private List<CartItem> getCart(HttpSession session) {
//        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
//        if (cart == null) {
//            cart = new ArrayList<>();
//            session.setAttribute(CART_SESSION_KEY, cart);
//        }
//        return cart;
//    }
//
//    private BigDecimal calculateTotal(List<CartItem> cart) {
//        return cart.stream()
//                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//
//}