package com.example.sweet_dreams.dto.review;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private String authorName;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
    private Long productId;
}
