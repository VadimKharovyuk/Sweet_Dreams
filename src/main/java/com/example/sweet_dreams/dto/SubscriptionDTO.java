package com.example.sweet_dreams.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscriptionDTO {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Пожалуйста, введите корректный email")
    private String email;
}
