package com.example.m08.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class PelangganController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerView(Model model) {
        model.addAttribute("pelanggan", new Pelanggan());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("pelanggan") Pelanggan pelanggan, 
                             BindingResult bindingResult, 
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        if (!pelanggan.getPassword().equals(pelanggan.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "user/register";
        }

        boolean success = userService.register(pelanggan);
        if (!success) {
            model.addAttribute("error", "Username already exists");
            return "user/register";
        }

        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {
        // Check if user is logged in
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");
        if (pelanggan == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", pelanggan);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Pelanggan pelanggan, HttpSession session) {
        userService.updateProfile(pelanggan);
        // Update the session with the new user data
        session.setAttribute("pelanggan", pelanggan);
        return "redirect:/profile?success";
    }

    @PostMapping("/topup")
    public String topUpSaldo(@RequestParam Double amount, HttpSession session) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");
        if (pelanggan == null) {
            return "redirect:/login";
        }
        
        userService.topUpSaldo(pelanggan.getUserId(), amount);
        // Update the session with the new balance
        pelanggan = userService.getCurrentUserProfile(pelanggan.getUsername());
        session.setAttribute("pelanggan", pelanggan);
        return "redirect:/profile?topup=success";
    }
}