package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomePage {
    private final CategoryService categoryService;

    @GetMapping
    public String home(Model model) {
        List<CategoryDto> listCategory = categoryService.getAllCategories();
        model.addAttribute("listCategory", listCategory);
        return "home";
    }

}
