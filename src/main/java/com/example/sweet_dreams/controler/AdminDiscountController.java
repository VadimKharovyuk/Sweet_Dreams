package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.discount.DiscountCreateDto;
import com.example.sweet_dreams.dto.discount.DiscountResponseDto;
import com.example.sweet_dreams.model.Discount;
import com.example.sweet_dreams.service.serviceImpl.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/admin/discounts")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService discountService;

    @PostMapping("/delete/{id}")  // Изменен путь
    public String deleteDiscount(@PathVariable Long id, RedirectAttributes redirectAttributes) {  // Добавлены RedirectAttributes
        try {
            discountService.deleteDiscount(id);
            redirectAttributes.addFlashAttribute("success", "Скидка успешно удалена");
            log.info("Discount with id {} was successfully deleted", id);
        } catch (Exception e) {
            log.error("Error deleting discount with id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении скидки");
        }
        return "redirect:/admin/discounts";
    }

    @GetMapping
    public String listDiscounts(Model model) {
        model.addAttribute("discounts", discountService.getAllDiscounts());
        return "admin/discounts/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("discount")) {
            model.addAttribute("discount", new DiscountCreateDto());
        }
        model.addAttribute("discountTypes", Discount.DiscountType.values());
        return "admin/discounts/form";
    }

    @PostMapping("/create")
    public String createDiscount(
            @ModelAttribute("discount") @Valid DiscountCreateDto discountDto,
            BindingResult result,
            Model model) {

        log.info("Attempting to create discount: {}", discountDto);

        // Дополнительная валидация дат
        if (discountDto.getValidFrom() != null &&
                discountDto.getValidFrom().isBefore(LocalDateTime.now())) {
            result.rejectValue("validFrom", "future",
                    "Дата начала должна быть в будущем");
        }

        // Дополнительная валидация значения скидки
        if (discountDto.getType() != null &&
                discountDto.getValue() != null &&
                discountDto.getType().isPercentage() &&
                discountDto.getValue().compareTo(new BigDecimal("100")) > 0) {
            result.rejectValue("value", "max",
                    "Процентная скидка не может превышать 100%");
        }

        if (result.hasErrors()) {
            log.error("Validation errors: {}", result.getAllErrors());
            return "admin/discounts/form";
        }

        try {
            DiscountResponseDto createdDiscount = discountService.createDiscount(discountDto);
            log.info("Successfully created discount with ID: {}", createdDiscount.getId());
            return "redirect:/admin/discounts";
        } catch (Exception e) {
            log.error("Error creating discount", e);
            model.addAttribute("error", "Ошибка при создании скидки: " + e.getMessage());
            return "admin/discounts/form";
        }
    }

}