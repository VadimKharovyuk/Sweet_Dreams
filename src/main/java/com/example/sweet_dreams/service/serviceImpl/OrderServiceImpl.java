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
    public Order create(OrderDTO orderDTO) {
        // Загружаем все продукты одним запросом
        List<Long> productIds = orderDTO.getItems().stream()
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toList());

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Проверяем, все ли продукты найдены
        if (productIds.size() != productMap.size()) {
            List<Long> notFoundIds = productIds.stream()
                    .filter(id -> !productMap.containsKey(id))
                    .toList();
            throw new EntityNotFoundException("Products not found: " + notFoundIds);
        }

        Order order = new Order();
        // Установка основных полей заказа
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setCustomerPhone(orderDTO.getCustomerPhone());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setStatus(Order.OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        // Создаем список позиций заказа
        List<OrderItem> items = orderDTO.getItems().stream()
                .map(itemDto -> {
                    Product product = productMap.get(itemDto.getProductId());
                    OrderItem orderItem = orderMapper.toEntity(itemDto, order, product);
                    // Устанавливаем текущую цену продукта
                    orderItem.setPrice(product.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(items);

        // Рассчитываем общую сумму заказа
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
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
}