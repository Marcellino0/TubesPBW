package com.example.m08.AdminMovie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.m08.RequiredRole;
import com.example.m08.Movie.MovieRepository;
import com.example.m08.Rental.MovieRentalStats;
import com.example.m08.User.Pelanggan;
import com.example.m08.User.PelangganRepository;
import com.example.m08.Rental.RentalRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PelangganRepository pelangganRepository;

    @Autowired
    private RentalRepository rentalRepository;

    // Endpoint dashboard admin 
    @GetMapping("/dashboard")
    @RequiredRole("admin") // Hanya admin yang bisa akses
    public String dashboard(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        return "admin/admindashboard";
    }

    // Endpoint untuk mengelola data pelanggan
    @GetMapping("/manage-customers")
    @RequiredRole("admin")
    public String manageCustomers(Model model) {
        model.addAttribute("customers", pelangganRepository.findAll());
        return "admin/kelolaPelanggan";
    }
    // Endpoint untuk mengedit data pelanggan
    @GetMapping("/edit-customer/{id}")
    @RequiredRole("admin")
    public String editCustomer(@PathVariable int id, Model model) {
        Pelanggan customer = pelangganRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        model.addAttribute("customer", customer);
        return "admin/editPelanggan";
    }

    // Endpoint untuk mengupdate data pelanggan
    @PostMapping("/update-customer/{id}")
    @RequiredRole("admin")
    public String updateCustomer(@PathVariable int id, @ModelAttribute Pelanggan customer, 
                               BindingResult result) {
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
    
    // Endpoint untuk mengdelete data pelanggan
    @GetMapping("/delete-customer/{id}")
    @RequiredRole("admin")
    public String deleteCustomer(@PathVariable int id) {
        pelangganRepository.deleteById(id);
        return "redirect:/admin/manage-customers";
    }

    // Endpoint untuk melihat laporan rental film
    @GetMapping("/reports")
    @RequiredRole("admin")
    public String viewReports(Model model) {
        List<MovieRentalStats> rentalStats = rentalRepository.getMovieRentalStats();
        model.addAttribute("rentalStats", rentalStats);
        return "admin/report";
    }

}