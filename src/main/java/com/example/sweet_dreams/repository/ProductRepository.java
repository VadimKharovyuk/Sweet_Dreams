package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Category;
import com.example.sweet_dreams.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByAvailableTrue();
    List<Product> findTop4ByCategoryIdAndAvailableTrueOrderByCreatedAtDesc(Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.reviews r " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "GROUP BY p " +
            "HAVING :minRating IS NULL OR COALESCE(AVG(CAST(r.rating AS double)), 0.0) >= :minRating")
    Page<Product> findWithFilters2(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") Double minRating,
            Pageable pageable
    );


    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.id != :id")
    Page<Product> findByCategoryAndIdNot(
            @Param("category") Category category,
            @Param("id") Long id,
            Pageable pageable);

    // Метод для получения популярных продуктов
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.reviews r " +
            "WHERE p.id != :excludeId " +
            "GROUP BY p " +
            "ORDER BY COALESCE(AVG(CAST(r.rating AS double)), 0) DESC")
    Page<Product> findTopByOrderByAverageRatingDesc(
            @Param("excludeId") Long excludeId,
            Pageable pageable);
}
