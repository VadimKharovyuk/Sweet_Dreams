package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.category.CategoryCreateDto;
import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.category.CategoryUpdateDto;
import com.example.sweet_dreams.service.serviceImpl.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryCreateDto", new CategoryCreateDto());
        return "admin/categories/create";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute CategoryCreateDto categoryCreateDto,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            return "admin/categories/create";
        }

        try {
            categoryService.createCategory(categoryCreateDto);
            return "redirect:/admin/categories?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/categories/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        model.addAttribute("category", categoryDto);
        return "admin/categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute CategoryUpdateDto categoryUpdateDto,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            return "admin/categories/edit";
        }

        try {
            categoryService.updateCategory(id, categoryUpdateDto);
            return "redirect:/admin/categories?updated";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/categories/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories?deleted";
    }
}
