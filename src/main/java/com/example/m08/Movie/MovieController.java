package com.example.m08.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


@Controller
@RequestMapping("/admin/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    // @GetMapping("/admin/dashboard")
    // public String dashboard(Model model, HttpSession session) {
    //     if (!isAdminAuthenticated(session)) {
    //         return "redirect:/loginadmin";
    //     }
        
    //     List<Movie> movies = movieRepository.findAll();
    //     model.addAttribute("movies", movies);
    //     return "admin/dashboard";
    // }

    @GetMapping("/manage")
    public String manageMovies(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        model.addAttribute("movie", new Movie());
        return "admin/kelolaFilm"; 
    }
    @PostMapping("/add")
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
            return "redirect:/admin/movies/manage?error=true";
        }
    
        movieRepository.save(movie);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable int id, Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        Movie movie = movieRepository.findById(id);
        if (movie == null) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("movie", movie);
        return "admin/editMovie";
    }

    @PostMapping("/edit/{id}")
    public String updateMovie(@PathVariable int id, 
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
                movie.setCover(existingMovie.getCover());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/movies/edit/" + id + "?error=true";
        }

        movie.setFilmId(id);
        movieRepository.update(movie);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable int id, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/loginadmin";
        }
        movieRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}