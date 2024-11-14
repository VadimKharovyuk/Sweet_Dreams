package com.example.sweet_dreams.controler;

import com.example.sweet_dreams.dto.admin.AdminCreateDto;
import com.example.sweet_dreams.exception.AdminAlreadyExistsException;
import com.example.sweet_dreams.model.Admin;
import com.example.sweet_dreams.repository.AdminRepository;
import com.example.sweet_dreams.service.serviceImpl.AdminService;
import com.example.sweet_dreams.service.serviceImpl.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Проверяем, аутентифицирован ли админ
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("adminUsername", session.getAttribute("adminUsername"));
        return "admin/dashboard";
    }

@GetMapping("/orders")
public String orders(Model model) {
    model.addAttribute("orders", orderService.findAll());
    return "admin/orders-list";
}




    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("adminCreateDto", new AdminCreateDto());
        return "admin/register";
    }


    @PostMapping("/register")
    public String registerAdmin(@Valid @ModelAttribute AdminCreateDto adminCreateDto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "admin/register";
        }
        try {
            adminService.createAdmin(adminCreateDto);
            return "redirect:/admin/login?registered";
        } catch (AdminAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/register";
        }
    }

    // Страница входа
    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();

            // Проверяем пароль
            if (adminService.checkPassword(admin, password)) {
                // Если пароль верный, сохраняем данные в сессии
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("adminRole", admin.getRole());
                session.setAttribute("adminUsername", admin.getUsername());

                // Перенаправляем на главную страницу админки
                return "redirect:/admin/dashboard";
            }
        }

        // Если логин неуспешен, возвращаемся на страницу логина с ошибкой
        model.addAttribute("error", "Неверное имя пользователя или пароль");
        return "admin/login";
    }


    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
//add to template
    @GetMapping("/list")
    public String listAdmins(Model model) {
        model.addAttribute("admins", adminService.findAllAdmins());
        return "admin/list";
    }
}
