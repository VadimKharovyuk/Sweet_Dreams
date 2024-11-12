package com.example.sweet_dreams.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewListDto {
    private Long id;
    private String authorName;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
