package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.model.Order;
import com.example.sweet_dreams.model.OrderItem;
import com.example.sweet_dreams.service.EmailService;
import com.example.sweet_dreams.service.serviceImpl.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/sent")
public class SentPostEmailController {
    private final EmailService emailService;
    private final OrderService orderService;

    @PostMapping("/send-email/{orderId}")
    @ResponseBody
    public Map<String, Object> sendOrderEmail(
            @PathVariable Long orderId,
            @RequestBody EmailRequestDTO emailRequest) {
        try {
            Order order = orderService.findById(orderId);
            if (order == null) {
                return Map.of("success", false, "message", "Заказ не найден");
            }

            emailService.sendEmail(
                    emailRequest.getEmail(),
                    "Ваш заказ №" + order.getId() + " в Sweet Dreams",
                    createOrderEmailContent(order)
            );

            return Map.of("success", true);
        } catch (Exception e) {
            log.error("Error sending order email for orderId: " + orderId, e);
            return Map.of("success", false, "message", "Ошибка при отправке");
        }
    }
    @Data
    private static class EmailRequestDTO {
        private String email;
        private boolean saveEmail;
    }

    private String createOrderEmailContent(Order order) {
        return String.format("""
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
            .header { background-color: #4f46e5; color: white; padding: 20px; text-align: center; }
            .order-details { margin: 20px 0; }
            .detail-row { margin: 10px 0; }
            .items-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
            .items-table th, .items-table td { padding: 10px; border: 1px solid #ddd; text-align: left; }
            .total { text-align: right; font-weight: bold; margin-top: 20px; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>Ваше замовлення #%d</h1>
            </div>
            <div class="order-details">
                <p><strong>Статус замовлення:</strong> %s</p>
                <p><strong>Дата замовлення:</strong> %s</p>
                
                <h2>Інформація про доставку</h2>
                <p><strong>Ім'я:</strong> %s</p>
                <p><strong>Email:</strong> %s</p>
                <p><strong>Телефон:</strong> %s</p>
                <p><strong>Адреса доставки:</strong> %s</p>
                
                <h2>Склад замовлення</h2>
                <table class="items-table">
                    <tr>
                        <th>Товар</th>
                        <th>Ціна</th>
                        <th>Кількість</th>
                        <th>Сума</th>
                    </tr>
                    %s
                </table>
                
                <div class="total">
                    <p><strong>Загалом:</strong> %s UAH</p>
                </div>
            </div>
        </div>
    </body>
    </html>
    """,
                order.getId(),
                order.getStatus().getDescription(),
                formatDateTime(order.getCreatedAt()),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getDeliveryAddress(),
                createOrderItemsRows(order.getItems()),
                formatPrice(order.getTotalAmount())
        );
    }


    private String createOrderItemsRows(List<OrderItem> items) {
        StringBuilder rows = new StringBuilder();
        for (OrderItem item : items) {
            rows.append(String.format(
                    "<tr>" +
                            "<td>%s</td>" +
                            "<td>%s UAH</td>" +
                            "<td>%d</td>" +
                            "<td>%s UAH</td>" +
                            "</tr>",
                    item.getProduct().getName(),
                    formatPrice(item.getPrice()),
                    item.getQuantity(),
                    formatPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            ));
        }
        return rows.toString();
    }


    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%.2f", price);
    }


}
