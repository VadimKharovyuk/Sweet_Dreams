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

    @Lob
    @Column(name = "main_image")
    private byte[] mainImage;


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



    // Статус и наличие
    @Column(name = "is_available")
    private boolean available = true;

    @Column(name = "preparation_time")
    private Integer preparationTimeHours;  // время приготовления в часах

    @Column(name = "minimum_order_time")
    private Integer minimumOrderTimeHours; // минимальное время для заказа

    // Рейтинг и отзывы
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(name = "reviews_count")
    private Integer reviewsCount = 0;



    // Цены для разных размеров
    @ElementCollection
    @CollectionTable(
            name = "product_size_prices",
            joinColumns = @JoinColumn(name = "product_id")
    )
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
        SMALL(14),    // 14 см
        MEDIUM(18),   // 18 см
        LARGE(22),    // 22 см
        EXTRA(26);    // 26 см

        private final int diameter;

        CakeSize(int diameter) {
            this.diameter = diameter;
        }

        public int getDiameter() {
            return diameter;
        }

        @Override
        public String toString() {
            return String.format("%d см", diameter);
        }
    }
}
