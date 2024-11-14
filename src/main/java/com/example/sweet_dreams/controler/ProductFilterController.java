package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductFilterDto;
import com.example.sweet_dreams.service.serviceImpl.CategoryService;
import com.example.sweet_dreams.service.serviceImpl.ProductService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ProductFilterController {
    private final ProductService productService;
    private final CategoryService categoryService; // Добавьте этот сервис

    @GetMapping("/filter")
    public String filterProducts(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        ProductFilterDto filterDto = ProductFilterDto.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoryId(categoryId)
                .minRating(minRating)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<ProductDto> productsPage = productService.findWithFilters(filterDto, page, size);

        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("currentFilters", filterDto);

        return "products/search";
    }
}