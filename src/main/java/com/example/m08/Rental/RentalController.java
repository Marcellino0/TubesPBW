package com.example.m08.Rental;

import com.example.m08.Movie.Movie;
import com.example.m08.Movie.MovieRepository;
import com.example.m08.User.Pelanggan;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class RentalController {
    private final MovieRepository movieRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public RentalController(MovieRepository movieRepository, RentalRepository rentalRepository) {
        this.movieRepository = movieRepository;
        this.rentalRepository = rentalRepository;
    }

    // Halaman rental
    @GetMapping("/rental")
    public String getRentalPage(Model model, HttpSession session) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");
        if (pelanggan == null) {
            return "redirect:/login";
        }
        
        List<RentalWithMovie> currentRentals = rentalRepository.findCurrentRentals();
        model.addAttribute("rentals", currentRentals);
        
        return "user/rental";
    }

    // API untuk melakukan rental
    @PostMapping("/api/rental/{filmId}")
    @ResponseBody
    public ResponseEntity<?> rentMovie(
        @PathVariable Long filmId,
        @RequestBody RentalRequest rentalRequest,
        HttpSession session
    ) {
        try {
            Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");
            if (pelanggan == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please login first");
            }

            Movie movie = movieRepository.findById(filmId);
            if (movie == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Movie not found");
            }

            if (movie.getStok() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Movie out of stock");
            }

            // Create rental
            LocalDate rentDate = LocalDate.parse(rentalRequest.getRentDate());
            LocalDate dueDate = rentDate.plusDays(rentalRequest.getDuration());

            Rental rental = new Rental();
            rental.setFilmId(filmId);
            rental.setRentDate(rentDate);
            rental.setDueDate(dueDate);
            rental.setStatus("ACTIVE");

            // Save rental
            rentalRepository.save(rental);

            // Update movie stock
            movie.setStok(movie.getStok() - 1);
            movieRepository.update(movie);

            return ResponseEntity.ok("Movie rented successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to rent movie: " + e.getMessage());
        }
    }

    // API untuk mengembalikan film
    @PostMapping("/api/rental/return/{rentalId}")
    @ResponseBody
    public ResponseEntity<?> returnMovie(@PathVariable Long rentalId, HttpSession session) {
        try {
            Pelanggan pelanggan = (Pelanggan) session.getAttribute("pelanggan");
            if (pelanggan == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Please login first");
            }

            Rental rental = rentalRepository.findById(rentalId);
            if (rental == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Rental not found");
            }

            rental.setStatus("RETURNED");
            rentalRepository.update(rental);

            // Increase movie stock
            Movie movie = movieRepository.findById(rental.getFilmId());
            movie.setStok(movie.getStok() + 1);
            movieRepository.update(movie);

            return ResponseEntity.ok().body("Movie returned successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to return movie: " + e.getMessage());
        }
    }
}