package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.message.ContactMessageDto;
import com.example.sweet_dreams.model.ContactMessage;
import com.example.sweet_dreams.service.serviceImpl.ContactMessageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactMessageController {
    private final ContactMessageService contactMessageService;

    @GetMapping
    public String showContactForm(Model model) {
        log.info("Отображение формы обратной связи");
        model.addAttribute("contactMessageDto", new ContactMessageDto());
        model.addAttribute("subjects", ContactMessage.ContactSubject.values());
        return "contact/contact-form";
    }

    @PostMapping
    public String submitContactForm(
            @Valid @ModelAttribute("contactMessageDto") ContactMessageDto contactMessageDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        log.info("Получена форма обратной связи: {}", contactMessageDto);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации формы: {}", bindingResult.getAllErrors());
            model.addAttribute("subjects", ContactMessage.ContactSubject.values());
            return "contact/contact-form";
        }

        try {
            contactMessageService.createContactMessage(contactMessageDto);
            log.info("Сообщение успешно создано");
            redirectAttributes.addFlashAttribute("successMessage",
                    "Спасибо! Ваше сообщение успешно отправлено. Мы свяжемся с вами в ближайшее время.");
            return "redirect:/contact";
        } catch (Exception e) {
            log.error("Ошибка при создании сообщения", e);
            model.addAttribute("errorMessage",
                    "Произошла ошибка при отправке сообщения. Пожалуйста, попробуйте позже.");
            model.addAttribute("subjects", ContactMessage.ContactSubject.values());
            return "contact/contact-form";
        }
    }

    // Админский интерфейс для просмотра сообщений
    @GetMapping("/admin/messages")
    public String listMessages(Model model) {
        try {
            List<ContactMessageDto> messages = contactMessageService.getAllMessages();
            model.addAttribute("messages", messages);
            return "contact/message-list";
        } catch (Exception e) {
            log.error("Ошибка при получении списка сообщений", e);
            model.addAttribute("errorMessage",
                    "Произошла ошибка при загрузке сообщений.");
            return "error";
        }
    }

    @GetMapping("/admin/messages/{id}")
    public String viewMessage(@PathVariable Long id, Model model) {
        try {
            ContactMessageDto message = contactMessageService.getContactMessageById(id);
            model.addAttribute("message", message);
            return "contact/message-details";
        } catch (EntityNotFoundException e) {
            log.warn("Сообщение не найдено: {}", id);
            model.addAttribute("errorMessage", "Сообщение не найдено");
            return "error";
        } catch (Exception e) {
            log.error("Ошибка при получении сообщения", e);
            model.addAttribute("errorMessage",
                    "Произошла ошибка при загрузке сообщения");
            return "error";
        }
    }

    @PostMapping("/admin/messages/{id}/delete")
    public String deleteMessage(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            contactMessageService.deleteContactMessageById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Сообщение успешно удалено");
        } catch (Exception e) {
            log.error("Ошибка при удалении сообщения", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Произошла ошибка при удалении сообщения");
        }
        return "redirect:/contact/admin/messages";
    }

    // Обработчик ошибок для всего контроллера
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        log.error("Непредвиденная ошибка", e);
        model.addAttribute("errorMessage",
                "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
        return "error";
    }
}
