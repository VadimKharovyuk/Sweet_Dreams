package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.dto.discount.OrderPricing;
import com.example.sweet_dreams.dto.order.CartItem;
import com.example.sweet_dreams.dto.order.OrderDTO;
import com.example.sweet_dreams.dto.order.OrderDetailsDTO;
import com.example.sweet_dreams.dto.order.OrderItemDTO;
import com.example.sweet_dreams.exception.ResourceNotFoundException;
import com.example.sweet_dreams.maper.OrderMapper;
import com.example.sweet_dreams.model.Order;
import com.example.sweet_dreams.model.OrderItem;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.repository.OrderRepository;
import com.example.sweet_dreams.repository.ProductRepository;
import com.example.sweet_dreams.service.serviceImpl.DiscountService;
import com.example.sweet_dreams.service.serviceImpl.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
  private final DiscountService discountService;

    @Override
    public Order create(OrderDTO orderDTO) {
        // Создаем заказ
        Order order = new Order();
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setCustomerPhone(orderDTO.getCustomerPhone());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setPrivateMessage(orderDTO.getPrivateMessage());
        order.setStatus(Order.OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        // Создаем и привязываем элементы заказа
        List<OrderItem> orderItems = createOrderItems(orderDTO, order);
        order.setItems(orderItems);

        // Рассчитываем цены с учетом скидки
        OrderPricing pricing = calculateOrderPricing(orderDTO, orderItems);

        // Применяем цены к заказу
        order.setSubtotalAmount(pricing.getSubtotal());
        order.setDiscount(pricing.getDiscount());
        order.setTotalAmount(pricing.getFinalPrice());
        order.setCouponCode(orderDTO.getCouponCode());

        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        // Если была применена скидка, увеличиваем счетчик использования
        if (pricing.getDiscount() != null && pricing.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            try {
                discountService.incrementUsageCount(
                        discountService.findByCode(orderDTO.getCouponCode()).getId()
                );
            } catch (Exception e) {
                log.error("Error incrementing coupon usage count", e);
            }
        }

        log.info("Created order: id={}, subtotal={}, discount={}, total={}",
                savedOrder.getId(),
                savedOrder.getSubtotalAmount(),
                savedOrder.getDiscount(),
                savedOrder.getTotalAmount());

        return savedOrder;
    }

    private List<OrderItem> createOrderItems(OrderDTO orderDTO, Order order) {
        return orderDTO.getItems().stream()
                .map(itemDto -> createOrderItem(itemDto, order))
                .collect(Collectors.toList());
    }

    private OrderPricing calculateOrderPricing(OrderDTO orderDTO, List<OrderItem> orderItems) {
        BigDecimal subtotal = calculateSubtotal(orderItems);
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal finalPrice = subtotal;

        // Проверяем наличие купона
        String couponCode = orderDTO.getCouponCode();
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            try {
                List<CartItem> cartItems = orderItems.stream()
                        .map(this::convertToCartItem)
                        .collect(Collectors.toList());

                if (discountService.isValidCoupon(couponCode, subtotal)) {
                    finalPrice = discountService.calculateFinalPrice(cartItems, couponCode);
                    discount = subtotal.subtract(finalPrice);
                    log.info("Applied discount: amount={}, coupon={}", discount, couponCode);
                } else {
                    log.info("Coupon {} is not valid, using original price", couponCode);
                }
            } catch (Exception e) {
                log.warn("Error applying coupon {}, using original price", couponCode, e);
            }
        } else {
            log.info("No coupon provided, using original price");
        }

        return new OrderPricing(subtotal, discount, finalPrice);
    }

    @Data
    @AllArgsConstructor
    private static class OrderPricing {
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal finalPrice;
    }

    // Вспомогательные методы
    private OrderItem createOrderItem(OrderItemDTO itemDto, Order order) {
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(itemDto.getQuantity())
                .price(itemDto.getPrice())
                .build();
    }

    private BigDecimal calculateSubtotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartItem convertToCartItem(OrderItem orderItem) {
        return CartItem.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }

    private void updateProductStock(List<OrderItem> items) {
        items.forEach(item -> {
            Product product = item.getProduct();
            // Здесь может быть логика обновления остатков товара
        });
    }

    private CartItem convertToCartItem(OrderItemDTO item) {
        return CartItem.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }


    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Data
    @Builder
    @AllArgsConstructor
    public static class OrderSummary {
        private int totalItems;           // Общее количество позиций
        private int totalUnits;           // Общее количество единиц товаров
        private BigDecimal subtotal;      // Общая стоимость
        private BigDecimal averageItemPrice; // Средняя стоимость товара
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    @Override
    // Новый метод для получения сводной информации о заказе
    public OrderSummary calculateOrderSummary(Order order) {
        List<OrderItem> items = order.getItems();

        // Подсчет количества товаров
        int totalItems = items.size();

        // Подсчет общей стоимости товаров
        BigDecimal subtotal = calculateTotal(items);

        // Подсчет общего количества единиц товаров
        int totalUnits = items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // Средняя стоимость товара
        BigDecimal averageItemPrice = totalItems > 0
                ? subtotal.divide(BigDecimal.valueOf(totalItems), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return OrderSummary.builder()
                .totalItems(totalItems)           // Общее количество позиций
                .totalUnits(totalUnits)          // Общее количество единиц товаров
                .subtotal(subtotal)              // Общая стоимость
                .averageItemPrice(averageItemPrice) // Средняя стоимость товара
                .build();
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<Order> findAll() {
//        return orderRepository.findAll();
//    }


    @Override
    public void delete(Long id) {
        Order order = findById(id);
        orderRepository.delete(order);
    }

    @Override
    public boolean isProductAvailable(Long productId) {
        return productRepository.findById(productId)
                .map(Product::isAvailable)
                .orElse(false);
    }

    @Override
    public Page<Order> searchOrders(String query, Order.OrderStatus status, Pageable pageable) {
        String searchQuery = query != null ? query.trim().toLowerCase() : null;
        Long searchId = null;

        if (searchQuery != null && searchQuery.matches("\\d+")) {
            try {
                searchId = Long.parseLong(searchQuery);
            } catch (NumberFormatException ignored) {
            }
        }

        return orderRepository.findBySearchCriteriaAndStatus(searchQuery, searchId, status, pageable);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    @Override
    public OrderDetailsDTO getOrderDetails(Long orderId) {
        Order order = findById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Заказ не найден");
        }

        BigDecimal subtotal = calculateOrderSubtotal(order);
        BigDecimal discount = order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO;

        return OrderDetailsDTO.builder()
                .order(order)
                .subtotal(subtotal)
                .discount(discount)
                .total(order.getTotalAmount())
                .appliedCouponCode(order.getCouponCode())
                .build();
    }

    private BigDecimal calculateOrderSubtotal(Order order) {
        return order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}