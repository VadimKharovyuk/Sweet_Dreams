package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.product.ProductCreateDto;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductUpdateDto;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.service.CategoryService;
import com.example.sweet_dreams.service.ImageService;
import com.example.sweet_dreams.service.MockMultipartFile;
import com.example.sweet_dreams.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ImageService imageService;

    // Показать список всех продуктов
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());

        return "admin/products/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("productCreateDto")) {
            model.addAttribute("productCreateDto", new ProductCreateDto());
        }
        List<CategoryDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("cakeSizes", Product.CakeSize.values());
        return "admin/products/create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductCreateDto productCreateDto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("cakeSizes", Product.CakeSize.values());
            return "admin/products/create";
        }

        try {
            // Обработка изображения если оно есть
            if (productCreateDto.getMainImage() != null && !productCreateDto.getMainImage().isEmpty()) {
                byte[] imageData = productCreateDto.getMainImage().getBytes();

                // Проверка размеров
                if (!imageService.isValidImageDimensions(imageData, 1920, 1080)) {
                    result.rejectValue("mainImage", "error.image",
                            "Изображение слишком большое. Максимальный размер: 1920x1080");
                    model.addAttribute("categories", categoryService.getAllCategories());
                    model.addAttribute("cakeSizes", Product.CakeSize.values());
                    return "admin/products/create";
                }

                // Оптимизация изображения
                byte[] optimizedImage = imageService.optimizeImage(imageData,
                        productCreateDto.getMainImage().getContentType());

                // Создаем временный MultipartFile с оптимизированным изображением
                MultipartFile optimizedFile = new MockMultipartFile(
                        productCreateDto.getMainImage().getOriginalFilename(),
                        productCreateDto.getMainImage().getOriginalFilename(),
                        "image/webp",
                        optimizedImage
                );

                // Устанавливаем оптимизированное изображение
                productCreateDto.setMainImage(optimizedFile);
            }

            productService.createProduct(productCreateDto);
            return "redirect:/admin/products?success";
        } catch (Exception e) {
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
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products?deleted";
    }

    // Изменение доступности продукта
    @PostMapping("/{id}/toggle-availability")
    public String toggleAvailability(@PathVariable Long id) {
        productService.toggleProductAvailability(id);
        return "redirect:/admin/products";
    }
}
