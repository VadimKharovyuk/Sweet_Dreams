package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.NewsletterForm;
import com.example.sweet_dreams.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/newsletter")
@RequiredArgsConstructor
@Slf4j
public class NewsletterController {
    private final EmailService emailService;

    @GetMapping
    public String showNewsletterForm(Model model) {
        model.addAttribute("newsletterForm", new NewsletterForm ());
        return "admin/newsletter";
    }

    @PostMapping("/send")
    public String sendNewsletter(@ModelAttribute @Valid NewsletterForm form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/newsletter";
        }

        try {
            String htmlContent = emailService.createNewsletterTemplate(
                    form.getTitle(),
                    form.getContent()
            );

            emailService.sendNewsletterToAllSubscribers(form.getTitle(), htmlContent);

            redirectAttributes.addFlashAttribute("success", "Розсилка успішно відправлена");
            log.info("Newsletter sent successfully");
        } catch (Exception e) {
            log.error("Error sending newsletter", e);
            redirectAttributes.addFlashAttribute("error",
                    "Помилка при відправці розсилки: " + e.getMessage());
        }

        return "redirect:/admin/newsletter";
    }
}
