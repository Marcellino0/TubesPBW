package com.example.m08.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerView(Model model) {
        model.addAttribute("pelanggan", new Pelanggan());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Pelanggan pelanggan, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!pelanggan.getPassword().equals(pelanggan.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "PasswordMismatch", "Passwords do not match");
            return "register";
        }

        boolean success = userService.register(pelanggan);
        if (!success) {
            bindingResult.rejectValue("username", "UsernameExists", "Username already exists");
            return "register";
        }

        return "redirect:/login";
    }
}