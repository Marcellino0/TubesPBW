package com.example.m08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.m08.AdminMovie.Admin;
import com.example.m08.AdminMovie.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginControllerAdmin {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/loginadmin")
    public String loginView(HttpSession session) {
        if (session.getAttribute("admin") != null) {
            return "redirect:/admin/dashboard";
        }
        return "login"; // Menggunakan template login.html
    }
    
    @PostMapping("/loginadmin")
    public String processLogin(@RequestParam String username, 
                             @RequestParam String password, 
                             HttpSession session, 
                             Model model) {
        Admin admin = adminService.login(username, password);
        if (admin == null) {
            model.addAttribute("error", "Invalid credentials");
            return "login"; // Menggunakan template login.html
        }
        
        session.setAttribute("admin", admin);
        session.setAttribute("role", "admin");
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginadmin";
    }
}