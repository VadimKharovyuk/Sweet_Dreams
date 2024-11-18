package com.example.sweet_dreams.dto.order;


import com.example.sweet_dreams.model.Order;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;

    @NotBlank(message = "Имя клиента обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String customerName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не должен превышать 255 символов")
    private String customerEmail;

    @NotBlank(message = "Номер телефона обязателен")
//    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный формат телефона")
    private String customerPhone;

    @NotBlank(message = "Адрес доставки обязателен")
    @Size(min = 10, max = 500, message = "Адрес должен быть от 10 до 500 символов")
    private String deliveryAddress;



    private List<OrderItemDTO> items;

    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;

    @Column(name = "private_message", length = 1000)
    private String privateMessage;

    private String couponCode;

}
