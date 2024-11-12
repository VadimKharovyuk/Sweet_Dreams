package com.example.sweet_dreams.maper;

import com.example.sweet_dreams.dto.review.*;
import com.example.sweet_dreams.exception.ProductNotFoundException;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.model.Review;
import com.example.sweet_dreams.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    private final ProductRepository productRepository;

    public Review toEntity(ReviewCreateDto dto) {
        if (dto == null) return null;

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + dto.getProductId()));

        // Обрезаем контент если он длиннее 1200 символов
        String content = dto.getContent();
        if (content != null && content.length() > 1200) {
            content = content.substring(0, 1200);
        }

        Review review = Review.builder()
                .product(product)
                .authorName(dto.getAuthorName().trim())
                .content(content.trim())
                .rating(dto.getRating())
                .createdAt(LocalDateTime.now())
                .build();

        // Обновляем счетчик отзывов в продукте
        product.setReviewsCount(product.getReviewsCount() + 1);
        product.getReviews().add(review);

        return review;
    }

    public void updateEntityFromDto(ReviewUpdateDto dto, Review review) {
        if (dto == null || review == null) return;

        Integer oldRating = review.getRating();

        if (dto.getContent() != null) {
            String content = dto.getContent().trim();
            if (!content.isEmpty()) {
                // Обрезаем контент если он длиннее 1200 символов
                if (content.length() > 1200) {
                    content = content.substring(0, 1200);
                }
                review.setContent(content);
            }
        }

        if (dto.getRating() != null) {
            review.setRating(dto.getRating());

            if (!dto.getRating().equals(oldRating)) {
                Product product = review.getProduct();
                double newAverageRating = product.getReviews().stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0);
            }
        }
    }

    public ReviewDto toDto(Review review) {
        if (review == null) return null;

        return ReviewDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .authorName(review.getAuthorName())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public ReviewListDto toListDto(Review review) {
        if (review == null) return null;

        return ReviewListDto.builder()
                .id(review.getId())
                .authorName(review.getAuthorName())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }


    public List<ReviewDto> toDtoList(List<Review> reviews) {
        if (reviews == null) return List.of();
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReviewListDto> toListDtoList(List<Review> reviews) {
        if (reviews == null) return List.of();
        return reviews.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }
}