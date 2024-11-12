package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.subscription.SubscriptionDTO;
import com.example.sweet_dreams.exception.AlreadySubscribedException;
import com.example.sweet_dreams.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public String subscribe(@Valid @ModelAttribute SubscriptionDTO subscriptionDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error",
                    "Пожалуйста, введите корректный email");
            return "redirect:/";
        }

        try {
            subscriptionService.subscribe(subscriptionDTO.getEmail());
            redirectAttributes.addFlashAttribute("success",
                    "Спасибо за подписку! Проверьте вашу почту.");
        } catch (AlreadySubscribedException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Этот email уже подписан на рассылку");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Произошла ошибка. Попробуйте позже.");
        }

        return "redirect:/";
    }

    @GetMapping("/unsubscribe")
    public String unsubscribe(@RequestParam String email,
                              RedirectAttributes redirectAttributes) {
        try {
            subscriptionService.unsubscribe(email);
            redirectAttributes.addFlashAttribute("success",
                    "Вы успешно отписались от рассылки");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Произошла ошибка при отписке");
        }
        return "redirect:/";
    }
}
