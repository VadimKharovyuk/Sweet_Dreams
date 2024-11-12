package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.review.ReviewCreateDto;
import com.example.sweet_dreams.dto.review.ReviewListDto;
import com.example.sweet_dreams.service.ProductService;
import com.example.sweet_dreams.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;

    @GetMapping
    public String showReviews(@PathVariable Long productId, Model model) {
        ProductDto product = productService.getProductById(productId);
        List<ReviewListDto> reviews = reviewService.getReviewsByProductId(productId);
        Double averageRating = reviewService.getAverageRatingForProduct(productId);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("newReview", new ReviewCreateDto());

        return "reviews/list";
    }

    @PostMapping
    public String createReview(
            @PathVariable Long productId,
            @Valid @ModelAttribute("newReview") ReviewCreateDto reviewDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("product", productService.getProductById(productId));
            model.addAttribute("reviews", reviewService.getReviewsByProductId(productId));
            model.addAttribute("averageRating", reviewService.getAverageRatingForProduct(productId));
            return "reviews/list";
        }

        reviewDto.setProductId(productId);
        reviewService.createReview(reviewDto);
        redirectAttributes.addFlashAttribute("message", "Отзыв успешно добавлен!");

        return "redirect:/products/" + productId + "/reviews";
    }

    @PostMapping("/{reviewId}/delete")
    public String deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            RedirectAttributes redirectAttributes) {

        reviewService.deleteReview(reviewId);
        redirectAttributes.addFlashAttribute("message", "Отзыв удален!");

        return "redirect:/products/" + productId + "/reviews";
    }
}