package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.order.OrderDTO;
import com.example.sweet_dreams.dto.order.OrderItemDTO;
import com.example.sweet_dreams.maper.OrderMapper;
import com.example.sweet_dreams.model.Order;
import com.example.sweet_dreams.model.OrderItem;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.repository.OrderRepository;
import com.example.sweet_dreams.repository.ProductRepository;
import com.example.sweet_dreams.service.serviceImpl.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order create(OrderDTO orderDTO) {
        Order order = new Order();
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setCustomerPhone(orderDTO.getCustomerPhone());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setStatus(Order.OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = orderDTO.getItems().stream()
                .map(itemDto -> createOrderItem(itemDto, order))
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order.setTotalAmount(calculateTotal(orderItems));

        return orderRepository.save(order);
    }


    private OrderItem createOrderItem(OrderItemDTO itemDto, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setPrice(itemDto.getPrice());
        // Устанавливаем связь с Product
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        orderItem.setProduct(product);
        return orderItem;
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
            } catch (NumberFormatException ignored) {}
        }

        return orderRepository.findBySearchCriteriaAndStatus(searchQuery, searchId, status, pageable);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

}