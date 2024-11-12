package com.example.sweet_dreams.service.serviceImpl;


import com.example.sweet_dreams.dto.review.ReviewCreateDto;
import com.example.sweet_dreams.dto.review.ReviewDto;
import com.example.sweet_dreams.dto.review.ReviewListDto;
import com.example.sweet_dreams.dto.review.ReviewUpdateDto;
import com.example.sweet_dreams.exception.ProductNotFoundException;
import com.example.sweet_dreams.exception.ReviewNotFoundException;
import com.example.sweet_dreams.maper.ReviewMapper;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.model.Review;
import com.example.sweet_dreams.repository.ProductRepository;
import com.example.sweet_dreams.repository.ReviewRepository;
import com.example.sweet_dreams.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewDto createReview(ReviewCreateDto createDto) {
        // Проверяем существование продукта
        Product product = productRepository.findById(createDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + createDto.getProductId()));

        // Создаем новый отзыв
        Review review = reviewMapper.toEntity(createDto);

        // Сохраняем отзыв
        review = reviewRepository.save(review);

        // Обновляем статистику продукта и сохраняем изменения
        product.setReviewsCount(product.getReviewsCount() + 1);
        productRepository.save(product);

        return reviewMapper.toDto(review);
    }

    @Override
    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
        return reviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Long id, ReviewUpdateDto updateDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        // Обновляем данные отзыва
        reviewMapper.updateEntityFromDto(updateDto, review);

        // Сохраняем обновленный отзыв
        review = reviewRepository.save(review);

        return reviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        // Обновляем статистику продукта
        Product product = review.getProduct();
        product.setReviewsCount(product.getReviewsCount() - 1);
        product.getReviews().remove(review);

        // Удаляем отзыв
        reviewRepository.delete(review);

        // Сохраняем изменения в продукте
        productRepository.save(product);
    }

    @Override
    public List<ReviewListDto> getReviewsByProductId(Long productId) {
        // Проверяем существование продукта
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }

        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviewMapper.toListDtoList(reviews);
    }

    @Override
    public boolean existsById(Long id) {
        return reviewRepository.existsById(id);
    }

    @Override
    public Double getAverageRatingForProduct(Long productId) {
        // Проверяем существование продукта
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }

        return reviewRepository.findAverageRatingByProductId(productId)
                .orElse(0.0);
    }

    @Override
    public Integer getReviewsCountForProduct(Long productId) {
        // Проверяем существование продукта
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }

        return reviewRepository.countByProductId(productId);
    }
}
