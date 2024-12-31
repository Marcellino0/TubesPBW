package com.example.m08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.m08.Movie.Movie;
import com.example.m08.Movie.MovieRepository;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import org.springframework.ui.Model;

@Controller
public class DashboardController {

    @Autowired
    MovieRepository movieRepository;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        List<Movie> topMovies = movieRepository.findTop3MoviesByRentals();
        List<Movie> latestMovies = movieRepository.findLast3Movies();
        model.addAttribute("topMovies", topMovies);
        model.addAttribute("latestMovies", latestMovies);
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