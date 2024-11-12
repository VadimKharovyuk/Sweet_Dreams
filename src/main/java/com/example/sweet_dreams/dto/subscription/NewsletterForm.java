package com.example.sweet_dreams.dto.subscription;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewsletterForm {
    @NotBlank(message = "Введіть заголовок")
    private String title;

    @NotBlank(message = "Введіть текст розсилки")
    private String content;
}
