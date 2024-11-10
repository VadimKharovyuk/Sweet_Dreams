package com.example.sweet_dreams.dto.category;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private String slug ;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}