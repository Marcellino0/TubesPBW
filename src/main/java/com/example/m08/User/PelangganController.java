package com.example.m08.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.m08.RequiredRole;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
public String registerUser(@Valid @ModelAttribute("pelanggan") Pelanggan pelanggan, 
                         BindingResult bindingResult, 
                         Model model) {
    // Validasi format input
    if (bindingResult.hasErrors()) {
        // Ambil pesan error dari validasi
        String errorMessage = bindingResult.getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Terjadi kesalahan validasi");
        model.addAttribute("error", errorMessage);
        return "user/register";
    }

    // Validasi password match
    if (!pelanggan.getPassword().equals(pelanggan.getConfirmPassword())) {
        model.addAttribute("error", "Password dan konfirmasi password tidak cocok");
        return "user/register";
    }

    try {
        boolean success = userService.register(pelanggan);
        if (!success) {
            model.addAttribute("error", "Username sudah digunakan, silakan pilih username lain");
            return "user/register";
        }
        
        model.addAttribute("success", "Registrasi berhasil! Silakan login.");
        return "redirect:/login";
    } catch (Exception e) {
        model.addAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        return "user/register";
    }
}

    @GetMapping("/profile")
    @RequiredRole("user")
    public String viewProfile(Model model, HttpSession session) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");
        model.addAttribute("user", pelanggan);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    @RequiredRole("user")
    public String updateProfile(@ModelAttribute Pelanggan pelanggan, HttpSession session) {
        userService.updateProfile(pelanggan);
        session.setAttribute("pelanggan", pelanggan);
        return "redirect:/profile?success";
    }

    @PostMapping("/topup")
    @RequiredRole("user")
    public String topUpSaldo(@RequestParam Double amount, 
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            if (amount < 10000) {
                redirectAttributes.addFlashAttribute("error", "Minimum top up amount is Rp 10.000");
                return "redirect:/profile";
            }

            Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");            
            userService.topUpSaldo(pelanggan.getUserId(), amount);
            
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