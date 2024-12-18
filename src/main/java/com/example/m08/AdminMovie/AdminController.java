package com.example.m08.AdminMovie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import com.example.m08.Movie.MovieRepository;
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
private MovieRepository movieRepository;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    @GetMapping("")
    public String adminRoot() {
        return "redirect:/loginadmin";
    }

    @GetMapping("/dashboard")
public String dashboard(Model model, HttpSession session) {
    if (!isAdminAuthenticated(session)) {
        return "redirect:/loginadmin";
    }
    model.addAttribute("movies", movieRepository.findAll());
    return "admin/admindashboard";
}

    @GetMapping("/registerAdmin")  // Changed from /register to /registerAdmin
    public String registerView(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        model.addAttribute("admin", new Admin());
        return "admin/registerAdmin";  // Make sure this matches your template path exactly
    }

    @PostMapping("/registerAdmin")  // Changed from /register to /registerAdmin
    public String registerUser(@ModelAttribute("admin") Admin admin,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/registerAdmin";
        }

        if (!admin.getPassword().equals(admin.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "admin/registerAdmin";
        }

        return "redirect:/loginadmin";
    }

    @GetMapping("/admin/manage-movies-admin")
    public String manageMovies(HttpSession session) {
    if (!isAdminAuthenticated(session)) {
        return "redirect:/loginadmin";
    }
    return "admin/kelolaFilm";
}

    @GetMapping("/manage-customers")
    public String manageCustomers(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        return "admin/kelolaPelanggan";
    }

    @GetMapping("/reports")
    public String viewReports(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        return "admin/report";
    }

    
}