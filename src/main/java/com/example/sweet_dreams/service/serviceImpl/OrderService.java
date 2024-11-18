package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.order.OrderDTO;
import com.example.sweet_dreams.dto.order.OrderDetailsDTO;
import com.example.sweet_dreams.model.Order;
import com.example.sweet_dreams.service.OrderServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrderService {

    OrderServiceImpl.OrderSummary calculateOrderSummary(Order order);
    Order create(OrderDTO orderDTO);

    Order findById(Long id);

    void updateOrderStatus(Long orderId, Order.OrderStatus newStatus);

    void delete(Long id);

    boolean isProductAvailable(Long productId);

    Page<Order> searchOrders(String query, Order.OrderStatus status, Pageable pageable);

    Order getOrderById(Long id);


    OrderDetailsDTO getOrderDetails(Long orderId);

}
