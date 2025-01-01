package com.example.m08.Rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.example.m08.User.Pelanggan;
import com.example.m08.User.PelangganRepository;
import com.example.m08.export.ExportRentalStatsPdf;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Controller
public class RentalController {
    
    @Autowired
    private RentalRepository rentalRepository;
    
    @Autowired
    private PelangganRepository pelangganRepository;

    @GetMapping("/rental")
    public String viewRentals(Model model, HttpSession session) {
        Pelanggan user = (Pelanggan) session.getAttribute("pelanggan");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("rentals", rentalRepository.findCurrentRentals());
        model.addAttribute("today", LocalDate.now());
        return "user/rental";
    }

    @GetMapping("/history-rental")
public String viewRentalHistory(Model model, HttpSession session) {
    Pelanggan user = (Pelanggan) session.getAttribute("pelanggan");
    if (user == null) {
        return "redirect:/login";
    }
    
    List<RentalHistory> rentalHistory = rentalRepository.findRentalHistory(user.getUserId());
    model.addAttribute("rentalHistory", rentalHistory);
    return "user/history-rental";
}

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

            // Attempt to save
            rentalRepository.save(rental, user.getUserId());

            // Update session with new balance
            user = pelangganRepository.findById(user.getUserId()).orElse(null);
            if (user != null) {
                session.setAttribute("pelanggan", user);
            }

            redirectAttributes.addFlashAttribute("success", "Movie rented successfully!");
            return "redirect:/rental";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/userdashboard";
        }
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

            if (rental.getDenda() > 0) {
                redirectAttributes.addFlashAttribute("warning", 
                    String.format("Movie returned with late fee: Rp%.2f", rental.getDenda()));
                
                user = pelangganRepository.findById(user.getUserId()).orElse(null);
                if (user != null) {
                    session.setAttribute("pelanggan", user);
                }
            } else {
                redirectAttributes.addFlashAttribute("success", "Movie returned successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to return movie: " + e.getMessage());
        }
        return "redirect:/rental";
    }

    @GetMapping("/rental/stats")
    public String viewRentalStats(Model model, HttpSession session) {
        Pelanggan user = (Pelanggan) session.getAttribute("pelanggan");
        if (user == null) {
            return "redirect:/login";
        }

        List<MovieRentalStats> stats = rentalRepository.getMovieRentalStats();
        model.addAttribute("rentalStats", stats);
        return "admin/reports";
    }

    @PostMapping("/admin/rental/update-target/{filmId}")
    @ResponseBody
    public ResponseEntity<?> updateMovieTarget(
        @PathVariable int filmId,
        @RequestBody TargetUpdateRequest request,
        HttpSession session) {
    
    try {
        rentalRepository.updateMovieTarget(filmId, request.getTargetCount());
        return ResponseEntity.ok()
            .body(Map.of("message", "Target updated successfully"));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    }
}

    @GetMapping("/api/rental/stats")
    @ResponseBody
    public ResponseEntity<List<MovieRentalStats>> getRentalStats() {
        try {
            List<MovieRentalStats> stats = rentalRepository.getMovieRentalStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rental/stats/export")
public ResponseEntity<byte[]> exportRentalStatsPdf() {
    try {
        List<MovieRentalStats> stats = rentalRepository.getMovieRentalStats();
        ByteArrayInputStream bis = ExportRentalStatsPdf.rentalStatsReport(stats);
        
        byte[] pdfBytes = bis.readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "rental_stats_report.pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

}