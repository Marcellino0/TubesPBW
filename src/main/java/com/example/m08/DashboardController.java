package com.example.m08;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
    
    @GetMapping("/")
    public String dashboard() {
        return "dashboard"; // Mengarah ke dashboard.html yang berisi landing page
    }
    
    @GetMapping("/home")
    public String redirectAfterLogin(HttpSession session) {
        if (session.getAttribute("admin") != null) {
            return "redirect:/admin/dashboard";
        } else if (session.getAttribute("pelanggan") != null) {
            return "redirect:/userdashboard";
        }
        return "redirect:/login";
    }
}