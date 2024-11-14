package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.review.ReviewCreateDto;
import com.example.sweet_dreams.dto.review.ReviewDto;
import com.example.sweet_dreams.dto.review.ReviewListDto;
import com.example.sweet_dreams.dto.review.ReviewUpdateDto;

import java.util.List;

public interface ReviewService {
    /**
     * Создает новый отзыв
     */
    ReviewDto createReview(ReviewCreateDto createDto);

    /**
     * Получает отзыв по ID
     */
    ReviewDto getReviewById(Long id);

    /**
     * Обновляет существующий отзыв
     */
    ReviewDto updateReview(Long id, ReviewUpdateDto updateDto);

    /**
     * Удаляет отзыв по ID
     */
    void deleteReview(Long id);

    /**
     * Получает все отзывы для продукта
     */
    List<ReviewListDto> getReviewsByProductId(Long productId);

    /**
     * Проверяет, существует ли отзыв с указанным ID
     */
    boolean existsById(Long id);

    /**
     * Получает среднюю оценку продукта
     */
    Double getAverageRatingForProduct(Long productId);

    /**
     * Получает количество отзывов для продукта
     */
    Integer getReviewsCountForProduct(Long productId);
}