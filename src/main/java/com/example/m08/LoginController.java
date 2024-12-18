package com.example.m08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.m08.User.Pelanggan;
import com.example.m08.User.UserService;
import com.example.m08.Movie.MovieRepository;
import com.example.m08.Movie.Movie;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("pelanggan") != null) {
            return "redirect:/userdashboard";
        }
        return "login";
    }
    
    @GetMapping("/userdashboard")
    public String userDashboardView(HttpSession session, Model model) {
        if (session.getAttribute("pelanggan") == null) {
            return "redirect:/login";
        }

        // Mengambil data film dari database
        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        
        // Menambahkan data user yang sedang login
        Pelanggan currentUser = (Pelanggan) session.getAttribute("pelanggan");
        model.addAttribute("user", currentUser);
        
        return "user/userdashboard";
    }
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                             @RequestParam String password, 
                             HttpSession session, 
                             Model model) {
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