package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.order.OrderDTO;
import com.example.sweet_dreams.dto.order.OrderItemDTO;
import com.example.sweet_dreams.maper.OrderMapper;
import com.example.sweet_dreams.model.Order;
import com.example.sweet_dreams.model.OrderItem;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.repository.OrderRepository;
import com.example.sweet_dreams.repository.ProductRepository;
import com.example.sweet_dreams.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateStatus(Long orderId, Order.OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

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
}