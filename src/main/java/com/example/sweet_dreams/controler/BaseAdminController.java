package com.example.sweet_dreams.controler;

import jakarta.servlet.http.HttpSession;

public abstract class BaseAdminController {
    protected String checkAdminAuth(HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        return null;
    }
}