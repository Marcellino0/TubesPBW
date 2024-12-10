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
public class MainController {
    
    @GetMapping("/")
    public String dashjbaord() {
        return "dashboard";
    }




}
