// RentalController.java
package com.example.m08.Rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

import com.example.m08.User.Pelanggan;
import com.example.m08.User.PelangganRepository;

@Controller
public class RentalController {
    
    @Autowired
    private RentalRepository rentalRepository;
    
    @Autowired
    private PelangganRepository pelangganRepository;

    @PostMapping("/rent/{filmId}")
    public String rentMovie(@PathVariable int filmId,
                          @RequestParam int duration,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            Pelanggan user = (Pelanggan) session.getAttribute("pelanggan");
            if (user == null) {
                return "redirect:/login";
            }

            // Create rental object
            Rental rental = new Rental();
            rental.setFilmId(filmId);
            rental.setRentDate(LocalDate.now());
            rental.setDueDate(LocalDate.now().plusDays(duration));
            rental.setStatus("ACTIVE");

            // Save rental
            rentalRepository.save(rental, user.getUserId());
            
            // Update session with new user data
            Pelanggan updatedUser = pelangganRepository.findById(user.getUserId()).orElse(null);
            if (updatedUser != null) {
                session.setAttribute("pelanggan", updatedUser);
            }
            
            redirectAttributes.addFlashAttribute("success", "Movie rented successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/userdashboard";
    }

    @GetMapping("/rental")
    public String viewRentals(Model model, HttpSession session) {
        Pelanggan user = (Pelanggan) session.getAttribute("pelanggan");
        if (user == null) {
            return "redirect:/login";
        }

        List<RentalWithMovie> rentals = rentalRepository.findCurrentRentals();
        model.addAttribute("rentals", rentals);
        return "user/rental";
    }

    @PostMapping("/rental/confirm-return/{rentalId}")
    public String confirmReturn(@PathVariable Long rentalId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Pelanggan user = (Pelanggan) session.getAttribute("pelanggan");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Instead of returning, we'll show confirmation
        return "redirect:/rental?confirm=" + rentalId;
    }

    @PostMapping("/rental/return/{rentalId}")
    public String returnMovie(@PathVariable Long rentalId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            Pelanggan user = (Pelanggan) session.getAttribute("pelanggan");
            if (user == null) {
                return "redirect:/login";
            }

            Rental rental = rentalRepository.findById(rentalId);
            if (rental == null) {
                redirectAttributes.addFlashAttribute("error", "Rental not found");
                return "redirect:/rental";
            }

            rental.setStatus("RETURNED");
            rentalRepository.update(rental);
            redirectAttributes.addFlashAttribute("success", "Movie returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rental";
    }
}