package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.product.ProductCreateDto;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductUpdateDto;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.service.serviceImpl.CategoryService;
import com.example.sweet_dreams.service.serviceImpl.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final CategoryService categoryService;


    // Показать список всех продуктов
    @GetMapping
    public String listProducts(Model model, HttpSession session) {
        model.addAttribute("products", productService.getAllProducts());
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }

        return "admin/products/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!model.containsAttribute("productCreateDto")) {
            model.addAttribute("productCreateDto", new ProductCreateDto());
        }
        List<CategoryDto> categories = categoryService.getAllCategories();

        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("categories", categories);
        model.addAttribute("cakeSizes", Product.CakeSize.values());
        return "admin/products/create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductCreateDto productCreateDto,
                                BindingResult result,
                                Model model) {
        log.info("Получен запрос на создание продукта: {}", productCreateDto);

        // Проверка обязательных полей
        if (productCreateDto.getSize() == null) {
            result.rejectValue("size", "error.size",
                    "Необходимо выбрать размер торта");
        }

        if (productCreateDto.getPrice() == null || productCreateDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            result.rejectValue("price", "error.price",
                    "Цена должна быть положительной");
        }

        if (result.hasErrors()) {
            log.error("Ошибки валидации: {}", result.getAllErrors());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("cakeSizes", Product.CakeSize.values());
            return "admin/products/create";
        }

        try {
            ProductDto createdProduct = productService.createProduct(productCreateDto);
            log.info("Продукт успешно создан: {}", createdProduct);
            return "redirect:/admin/products?success";
        } catch (Exception e) {
            log.error("Ошибка при создании продукта", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("cakeSizes", Product.CakeSize.values());
            return "admin/products/create";
        }
    }

    // Форма редактирования
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductDto productDto = productService.getProductById(id);
        model.addAttribute("productDto", productDto);
        model.addAttribute("cakeSizes", Product.CakeSize.values());
        return "admin/products/edit";
    }

    // Обработка редактирования
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute ProductUpdateDto updateDto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cakeSizes", Product.CakeSize.values());
            return "admin/products/edit";
        }

        try {
            productService.updateProduct(id, updateDto);
            return "redirect:/admin/products?updated";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/products/edit";
        }
    }

    // Удаление продукта
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        productService.deleteProduct(id);
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        return "redirect:/admin/products?deleted";
    }

    // Изменение доступности продукта
    @PostMapping("/{id}/toggle-availability")
    public String toggleAvailability(@PathVariable Long id) {
        productService.toggleProductAvailability(id);
        return "redirect:/admin/products";
    }
}
