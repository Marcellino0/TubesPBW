package com.example.m08;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/public")
    @RequiredRole({"user", "admin"})
    public String publicPage() {
        return "public";
    }
    
    @GetMapping("/admin")
    @RequiredRole("admin")
    public String adminPage() {
        return "admin";
    }
}