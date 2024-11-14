package com.example.sweet_dreams.controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller


public class PolicyController {
    @GetMapping("/policy")
    public String showPolicy() {
        return "policy";
    }
}
