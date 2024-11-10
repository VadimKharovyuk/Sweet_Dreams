package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.order.OrderDTO;
import com.example.sweet_dreams.model.Order;

import java.util.List;



public interface OrderService {
    Order create(OrderDTO orderDTO);
    Order findById(Long id);
    List<Order> findAll();
    Order updateStatus(Long orderId, Order.OrderStatus status);
    void delete(Long id);

}
