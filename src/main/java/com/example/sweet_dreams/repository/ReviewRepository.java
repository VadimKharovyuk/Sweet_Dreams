package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * Находит все отзывы для продукта, отсортированные по дате создания (сначала новые)
     */
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    /**
     * Подсчитывает количество отзывов для продукта
     */
    Integer countByProductId(Long productId);

    /**
     * Вычисляет средний рейтинг для продукта
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Long productId);
}
