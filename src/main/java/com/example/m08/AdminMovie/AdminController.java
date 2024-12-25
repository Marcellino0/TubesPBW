package com.example.m08.AdminMovie;

import com.example.m08.Movie.MovieRepository;
import com.example.m08.Rental.MovieRentalStats;
import com.example.m08.User.Pelanggan;
import com.example.m08.User.PelangganRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.m08.Rental.RentalRepository;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PelangganRepository pelangganRepository;

    @Autowired
    private RentalRepository rentalRepository;

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

    @GetMapping("/manage-customers")
    public String manageCustomers(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        model.addAttribute("customers", pelangganRepository.findAll());
        return "admin/kelolaPelanggan";
    }

    @GetMapping("/edit-customer/{id}")
    public String editCustomer(@PathVariable int id, Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        
        Pelanggan customer = pelangganRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        model.addAttribute("customer", customer);
        return "admin/editPelanggan";
    }

    @PostMapping("/update-customer/{id}")
    public String updateCustomer(@PathVariable int id, @ModelAttribute Pelanggan customer, 
                               BindingResult result, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }

        if (result.hasErrors()) {
            return "admin/editPelanggan";
        }

        Pelanggan existingCustomer = pelangganRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        existingCustomer.setUsername(customer.getUsername());
        existingCustomer.setNama(customer.getNama());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setNoTelp(customer.getNoTelp());
        
        pelangganRepository.save(existingCustomer);
        return "redirect:/admin/manage-customers";
    }

    @GetMapping("/delete-customer/{id}")
    public String deleteCustomer(@PathVariable int id, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        
        pelangganRepository.deleteById(id);
        return "redirect:/admin/manage-customers";
    }

    @GetMapping("/reports")
    public String viewReports(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        
        List<MovieRentalStats> rentalStats = rentalRepository.getMovieRentalStats();
        model.addAttribute("rentalStats", rentalStats);
        return "admin/report";
    }
}