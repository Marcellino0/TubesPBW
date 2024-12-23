package com.example.m08.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PelangganController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

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
}