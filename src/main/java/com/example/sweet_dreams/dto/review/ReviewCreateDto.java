package com.example.sweet_dreams.dto.review;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDto {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Author name is required")
    @Size(max = 255, message = "Author name must not exceed 255 characters")
    private String authorName;

    @NotBlank(message = "Review content is required")
    @Size(max = 1200, message = "Review content must not exceed 1200 characters")
    private String content;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
}