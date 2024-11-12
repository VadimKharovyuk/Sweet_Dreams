package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductListDto;
import com.example.sweet_dreams.service.CategoryService;
import com.example.sweet_dreams.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")  // для пользовательской части используем путь без /admin
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String showProducts(Model model) {

        List<CategoryDto> categories = categoryService.getAllCategories();
        List<ProductListDto> products = productService.getAllAvailableProducts();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "products/catalog";
    }

    @GetMapping("/category/{categoryId}")
    public String showProductsByCategory(@PathVariable Long categoryId, Model model) {
        List<ProductDto> products = productService.getProductsByCategory(categoryId);
        List<CategoryDto> categories = categoryService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        return "products/catalog";
    }



    @GetMapping("/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        ProductDto product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "products/details";
    }
}
