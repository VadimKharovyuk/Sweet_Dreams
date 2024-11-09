package com.example.sweet_dreams.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HomePage {

    @GetMapping
    public String home() {
        return "home";
    }

}
