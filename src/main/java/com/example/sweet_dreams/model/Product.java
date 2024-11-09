package com.example.sweet_dreams.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Основная информация
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private CakeSize size;

    @Column(name = "weight")
    private Double weight;  // вес в кг

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "product_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            indexes = @Index(name = "idx_ingredient_product", columnList = "product_id")
    )
    @Column(name = "ingredient", length = 100, nullable = false)
    @Builder.Default
    private Set<String> ingredients = new HashSet<>();



    @Column(name = "is_custom")
    private boolean custom;  // можно ли кастомизировать торт

    // Изображения
    @Column(name = "main_image")
    private String mainImage;


    // Статус и наличие
    @Column(name = "is_available")
    private boolean available = true;

    @Column(name = "preparation_time")
    private Integer preparationTimeHours;  // время приготовления в часах

    @Column(name = "minimum_order_time")
    private Integer minimumOrderTimeHours; // минимальное время для заказа

    // Рейтинг и отзывы
    @OneToMany(mappedBy = "product")
    private List<Review> averageRating;

    @Column(name = "reviews_count")
    private Integer reviewsCount = 0;



    // Цены для разных размеров
    @ElementCollection
    @CollectionTable(name = "product_size_prices",
            joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "size")
    @Column(name = "price")
    private Map<CakeSize, BigDecimal> sizePrices = new HashMap<>();


    // Аудит
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public enum CakeSize {
        SMALL(1.0),    // 1 кг
        MEDIUM(2.0),   // 2 кг
        LARGE(3.0),    // 3 кг
        EXTRA(5.0);    // 5 кг

        private final double weight;

        CakeSize(double weight) {
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }
    }
}
