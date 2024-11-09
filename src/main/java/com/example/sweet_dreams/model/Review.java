package com.example.sweet_dreams.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String authorName;
    private String content;
    private Integer rating;  // 1-5

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
