package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.category.CategoryWithProductsDto;
import com.example.sweet_dreams.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomePage {
    private final CategoryService categoryService;


    @GetMapping
    public String home(Model model) {
        // Получаем список категорий с продуктами и ограничиваем до 4
        List<CategoryWithProductsDto> categoriesWithProducts = categoryService.getAllCategoriesWithProducts()
                .stream()
                .limit(4)  // Ограничиваем до 4 категорий
                .collect(Collectors.toList());

        model.addAttribute("categories", categoriesWithProducts);
        model.addAttribute("listCategory", categoryService.getAllCategories()); // для навигации

        return "home";
    }

//    @GetMapping
//    public String home(Model model) {
//        // Получаем список категорий с продуктами
//        List<CategoryWithProductsDto> categoriesWithProducts = categoryService.getAllCategoriesWithProducts();
//
//        // Добавляем в модель
//        model.addAttribute("categories", categoriesWithProducts);
//        model.addAttribute("listCategory", categoryService.getAllCategories()); // для навигации
//
//        return "home";
//    }

}
