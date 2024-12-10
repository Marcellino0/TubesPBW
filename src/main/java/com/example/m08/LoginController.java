package com.example.m08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.m08.User.Pelanggan;
import com.example.m08.User.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;

  
    
    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("pelanggan") != null) {
            return "redirect:/userdashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerView(HttpSession session) {
        if (session.getAttribute("pelanggan") != null) {
            return "redirect:/userdashboard";
        }
        return "register";
    }
    
    @GetMapping("/userdashboard")
    public String userDashboardView(HttpSession session) {
        if (session.getAttribute("pelanggan") == null) {
            return "redirect:/login";
        }
        return "userdashboard";
    }
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Pelanggan pelanggan = userService.login(username, password);
        if (pelanggan == null) {
            model.addAttribute("status", "failed");
            return "login";
        }
        
        session.setAttribute("pelanggan", pelanggan);
        return "redirect:/userdashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("pelanggan");
        return "redirect:/login";
    }
}