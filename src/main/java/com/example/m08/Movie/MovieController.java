package com.example.m08.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Controller

public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    @GetMapping("/admin/manage-movies")
    public String manageMovies(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        model.addAttribute("movie", new Movie());
        return "admin/kelolaFilm";
    }

    @PostMapping("/admin/manage-movies")
    public String addMovie(@ModelAttribute Movie movie, 
                          @RequestParam("coverImage") MultipartFile coverImage, 
                          Model model, 
                          HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
    
        try {
            movie.setCover(coverImage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
        }
    
        movieRepository.save(movie);
        model.addAttribute("movies", movieRepository.findAll());
    
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/edit-movie/{id}")
    public String editMovieForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        Movie movie = movieRepository.findById(id); // Langsung menggunakan findById karena return Movie
        if (movie == null) {
            // Handle movie not found
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("movie", movie);
        return "admin/editMovie";
    }

    @PostMapping("/admin/edit-movie/{id}")
    public String updateMovie(@PathVariable Long id, 
                            @ModelAttribute Movie movie, 
                            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage, 
                            HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }

        Movie existingMovie = movieRepository.findById(id);
        if (existingMovie == null) {
            return "redirect:/admin/dashboard";
        }

        try {
            if (coverImage != null && !coverImage.isEmpty()) {
                movie.setCover(coverImage.getBytes());
            } else {
                // Gunakan cover yang sudah ada
                movie.setCover(existingMovie.getCover());
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
        }

        movie.setFilmId(id);
        movieRepository.save(movie);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/delete-movie/{id}")
    public String deleteMovie(@PathVariable Long id, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        movieRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}