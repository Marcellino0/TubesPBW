package com.example.m08.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String topUpSaldo(@RequestParam Double amount, 
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            // Validasi minimum amount
            if (amount < 10000) {
                redirectAttributes.addFlashAttribute("error", "Minimum top up amount is Rp 10.000");
                return "redirect:/profile";
            }

            Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");
            if (pelanggan == null) {
                return "redirect:/login";
            }
            
            userService.topUpSaldo(pelanggan.getUserId(), amount);
            
            // Update session dengan saldo baru
            pelanggan = userService.getCurrentUserProfile(pelanggan.getUsername());
            session.setAttribute("pelanggan", pelanggan);
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Successfully topped up Rp %.0f", amount));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}