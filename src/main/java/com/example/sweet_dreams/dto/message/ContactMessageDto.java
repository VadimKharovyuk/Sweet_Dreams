package com.example.sweet_dreams.dto.message;

import com.example.sweet_dreams.model.ContactMessage;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageDto {
    private Long id;
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private String lastName;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Неверный формат номера телефона")
    private String phoneNumber;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotNull(message = "Тема обращения обязательна")
    private ContactMessage.ContactSubject subject;

    @NotBlank(message = "Сообщение обязательно")
    @Size(min = 10, max = 1000, message = "Сообщение должно быть от 10 до 1000 символов")
    private String message;

    private LocalDateTime createdAt;
}
